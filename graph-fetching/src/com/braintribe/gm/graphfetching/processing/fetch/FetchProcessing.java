package com.braintribe.gm.graphfetching.processing.fetch;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import com.braintribe.common.attribute.AttributeContext;
import com.braintribe.common.lcd.Pair;
import com.braintribe.gm.graphfetching.api.FetchParallelization;
import com.braintribe.gm.graphfetching.api.node.AbstractEntityGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityRelatedPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.FetchQualification;
import com.braintribe.gm.graphfetching.api.node.PropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.ScalarCollectionPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.query.FetchQuery;
import com.braintribe.gm.graphfetching.api.query.FetchQueryFactory;
import com.braintribe.gm.graphfetching.api.query.FetchResults;
import com.braintribe.gm.graphfetching.api.query.FetchSource;
import com.braintribe.gm.graphfetching.processing.query.GmSessionFetchQueryFactory;
import com.braintribe.logging.Logger;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.AbsentEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.LinearCollectionType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.value.PreliminaryEntityReference;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.utils.collection.impl.AttributeContexts;
import com.braintribe.utils.lcd.CollectionTools2;
import com.braintribe.utils.lcd.Lazy;

/**
 * Implements the orchestration ('processing') for deep entity fetches according to a fetch graph (EntityGraphNode trees). Core idea: Split fetches
 * into tasks (to-one, to-many), schedule/queue them, and process recursively in breadth/depth. Extensible via FetchTask and FetchType.
 */
public class FetchProcessing implements FetchContext {
	protected static final int DEFAULT_BULK_SIZE = 100;
	private static Logger logger = Logger.getLogger(FetchProcessing.class);
	private Queue<FetchTask> taskQueue = new LinkedList<>();

	private AtomicInteger parallelTaskCounter = new AtomicInteger();
	private ConcurrentLinkedQueue<Future<?>> futures = new ConcurrentLinkedQueue<>();
	private ReentrantLock parallelDoneMonitor = new ReentrantLock();
	private Condition parallelDoneCondition = parallelDoneMonitor.newCondition();
	private PersistenceGmSession session;
	private Map<Pair<EntityType<?>, Object>, EntityIdm> index = new ConcurrentHashMap<>();
	private ExecutorService threadPool;
	private final FetchQueryFactory queryFactory;
	private int maxParallel = 10;
	private Lazy<Semaphore> lazySemaphore = new Lazy<>(this::buildSemaphore);
	private Lazy<ExecutorService> defaultExecutorLazy = new Lazy<>(() -> Executors.newFixedThreadPool(maxParallel), s -> s.shutdown());
	private int bulkSize = DEFAULT_BULK_SIZE;
	private int toOneScalarStopThreshold = 500;
	private double joinProbabiltyThreshold = 0.05;
	private double joinProbabilityDefault = 0.5;
	private FetchParallelization parallelization;
	private boolean polymorphicJoin = true;
	private List<Throwable> errors = Collections.synchronizedList(new ArrayList<>());
	private int toOneJoinThreshold = 1;

	public FetchProcessing(PersistenceGmSession session) {
		this(session, new GmSessionFetchQueryFactory(session));
	}

	public FetchProcessing(PersistenceGmSession session, FetchQueryFactory fetchQueryFactory) {
		this.session = session;
		this.queryFactory = fetchQueryFactory;
	}

	public void setExecutorService(ExecutorService executorService) {
		this.threadPool = executorService;
	}

	@Override
	public int bulkSize() {
		return bulkSize;
	}

	@Override
	public int toOneSelectCountStopThreshold() {
		return toOneScalarStopThreshold;
	}

	@Override
	public double defaultJoinProbability() {
		return joinProbabilityDefault;
	}

	@Override
	public double joinProbabiltyThreshold() {
		return joinProbabiltyThreshold;
	}
	
	@Override
	public double toOneJoinThreshold() {
		return toOneJoinThreshold;
	}
	
	@Override
	public boolean polymorphicJoin() {
		return polymorphicJoin;
	}

	@Override
	public void putEntity(EntityType<?> baseType, EntityIdm entityIdm) {
		index.putIfAbsent(Pair.of(baseType, entityIdm.entity.getId()), entityIdm);
	}
	
	@Override
	public EntityIdm acquireEntity(GenericEntity entity) {
		return index.computeIfAbsent(Pair.of(entity.entityType(), entity.getId()), k -> {
			return new EntityIdm(entity);
		});
	}

	@Override
	public EntityIdm resolveEntity(EntityType<?> type, Object id) {
		return index.get(Pair.of(type, id));
	}

	@Override
	public PersistenceGmSession session() {
		return session;
	}

	private Semaphore buildSemaphore() {
		return new Semaphore(maxParallel);
	}

	/**
	 * Entry point for regular fetch: wraps entity list as indexed map and processes.
	 */
	public void fetch(EntityGraphNode node, Collection<? extends GenericEntity> entities) {
		fetch(node, entityIndex(entities));
	}

	/**
	 * Main scheduling: enqueue required fetch tasks for the given graph node and entities and process all.
	 */
	public void fetch(EntityGraphNode node, Map<Object, GenericEntity> entities) {
		long nanosStart = System.nanoTime();
		FetchQualification fqToOne = new FetchQualification(node, FetchType.TO_ONE);
		FetchQualification fqToMany = new FetchQualification(node, FetchType.TO_MANY);
		for (GenericEntity entity : entities.values()) {
			EntityIdm idm = acquireEntity(entity);
			idm.addHandled(fqToOne);
			idm.addHandled(fqToMany);
		}
		enqueueToOneIfRequired(node, entities);
		enqueueToManyIfRequired(node, entities);
		process();
		Duration duration = Duration.ofNanos(System.nanoTime() - nanosStart);
		logger.debug(() -> "consumed " + duration.toMillis() + " ms for graph fetching of " + entities.size() + " entities of node "
				+ getNodeDescription(node));
	}

	private Map<Object, GenericEntity> entityIndex(Collection<? extends GenericEntity> entities) {
		Map<Object, GenericEntity> index = new LinkedHashMap<Object, GenericEntity>();
		for (GenericEntity entity : entities) {
			index.put(entity.getId(), entity);
		}
		return index;
	}

	@Override
	public void enqueueToOneIfRequired(AbstractEntityGraphNode node, Map<Object, GenericEntity> entities) {
		if (node.hasEntityProperties() && !entities.isEmpty()) {
			enqueue(new FetchTask(node, FetchType.TO_ONE, entities));
		}
	}

	@Override
	public void enqueueToManyIfRequired(AbstractEntityGraphNode node, Map<Object, GenericEntity> entities) {
		if (node.hasCollectionProperties() && !entities.isEmpty()) {
			enqueue(new FetchTask(node, FetchType.TO_MANY, entities));
		}
	}

	private void enqueue(FetchTask task) {
		if (parallelization.isFetchParallel()) {
			notifyParallelTaskComissioned();

			AttributeContext attributeContext = AttributeContexts.peek();

			futures.add(defaultExecutorLazy.get().submit(() -> {
				AttributeContexts.with(attributeContext).run(() -> {
					processMonitored(task);
				});
			}));
		} else
			taskQueue.offer(task);
	}

	private void processMonitored(FetchTask task) {
		Semaphore semaphore = lazySemaphore.get();
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException(e);
		}

		try {
			processTask(task);
		} finally {
			semaphore.release();

			notifyParallelTaskDone();
		}
	}

	private void notifyParallelTaskComissioned() {
		parallelTaskCounter.incrementAndGet();
	}

	
	private void notifyParallelTaskDone() {
		if (parallelTaskCounter.decrementAndGet() == 0) {
			parallelDoneMonitor.lock();

			try {
				parallelDoneCondition.signal();
			} finally {
				parallelDoneMonitor.unlock();
			}
		}
	}

	private void process() {
		if (parallelization.isFetchParallel())
			processMultiThreaded();
		else
			processSingleThreaded();
	}

	private void processMultiThreaded() {
		parallelDoneMonitor.lock();
		try {
			try {
				parallelDoneCondition.await();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new RuntimeException(e);
			}
		} finally {
			parallelDoneMonitor.unlock();
		}

		List<Throwable> exceptions = new ArrayList<>();
		for (Future<?> future : futures) {
			try {
				future.get();
			} catch (Exception e) {
				exceptions.add(e);
			}
		}
		
		exceptions.addAll(errors);

		if (exceptions.isEmpty())
			return;

		RuntimeException e = new RuntimeException("Error while executing parallel fetch tasks");

		if (exceptions.size() == 1)
			e.initCause(exceptions.get(0));
		else {
			for (Throwable cause : exceptions) {
				e.addSuppressed(cause);
			}
		}

		throw e;
	}

	private void processSingleThreaded() {
		while (true) {
			FetchTask task = taskQueue.poll();

			if (task == null)
				break;

			processTask(task);
		}
		
	
		if (errors.isEmpty())
			return;

		RuntimeException e = new RuntimeException("Error while executing parallel fetch tasks");

		if (errors.size() == 1)
			e.initCause(errors.get(0));
		else {
			for (Throwable cause : errors) {
				e.addSuppressed(cause);
			}
		}

		throw e;
	}

	/**
	 * Task worker: decide if a TO_ONE or TO_MANY fetch is required, and process accordingly.
	 */
	private void processTask(FetchTask task) {
		long nanosStart = System.nanoTime();

		switch (task.fetchType) {
			case TO_MANY:
				processToManyTask(task);
				break;
			case TO_ONE:
				processToOneTask(task);
				break;
		}

		Duration duration = Duration.ofNanos(System.nanoTime() - nanosStart);
		logger.trace(() -> "consumed " + duration.toMillis() + " ms for graph " + task.fetchType + " fetching of " + task.entities.size()
				+ " entities of node: " + getNodeDescription(task.node));
	}

	private static String getNodeDescription(AbstractEntityGraphNode node) {
		return node.entityType().getShortName();
	}

	/**
	 * Handles deep to-one fetching with follow-up to-many fetches on descendants.
	 */
	private void processToOneTask(FetchTask task) {
		ToOneRecursiveFetching toOneFetching = new ToOneRecursiveFetching(this, task.node);
		toOneFetching.fetch(this, task);
	}

	/**
	 * Handles deep to-many fetching with possible further graph traversal.
	 */
	private void processToManyTask(FetchTask task) {
		ToManyFetching.fetch(this, task.node, task);
	}
	
	private abstract class FetchHandler {
		public abstract void handleResult(FetchResults results);
	}
	
	private abstract class AbstractEntityFetchHandler extends FetchHandler {
		public final List<EntityPropertyGraphNode> entityProperties = new ArrayList<>();
		private int pos;
		private EntityType<?> baseEntityType;
		private Consumer<EntityIdm> visitor;
		
		public AbstractEntityFetchHandler(AbstractEntityGraphNode node, FetchSource source, int pos) {
			this.pos = pos;
			boolean supportsSubTypeJoin = queryFactory().supportsSubTypeJoin(); 
			baseEntityType = node.entityType();
			
			for (EntityGraphNode specificNode : node.entityNodes()) {
				EntityType<?> specificEntityType = specificNode.entityType();
				
				boolean polymorphic = specificEntityType == baseEntityType;
				
				if (polymorphic && !supportsSubTypeJoin)
					continue;
				
				FetchSource effectiveSource = polymorphic ? source: source.as(specificEntityType);
				
				for (EntityPropertyGraphNode propertyNode: specificNode.entityProperties().values()) {
					entityProperties.add(propertyNode);
					effectiveSource.selectEntityId(propertyNode.property());
				}
			}
		}
		
		public void setVisitor(Consumer<EntityIdm> visitor) {
			this.visitor = visitor;
		}
		
		@Override
		public void handleResult(FetchResults results) {
			int i = pos;
			
			GenericEntity entity = results.get(i++);
			
			EntityIdm entityIdm = acquireEntity(entity);
			
			GenericEntity effectiveEntity = entityIdm.entity;
			
			if (baseEntityType != effectiveEntity.entityType())
				putEntity(baseEntityType, entityIdm);
			
			if (effectiveEntity == entity) {
				for (EntityPropertyGraphNode propertyNode: entityProperties) {
					Object refId = results.get(i++);
					Property property = propertyNode.property();
					
					if (refId == null) {
						property.setDirect(entity, null);
					}
					else {
						AbsentEntity absentEntity = AbsentEntity.create(property.getType().getTypeSignature(), refId);
						property.setVdDirect(effectiveEntity, absentEntity);
					}
				}
			}
			
			handleEntity(results, effectiveEntity);
			if (visitor != null)
				visitor.accept(entityIdm);
		}
		
		protected abstract void handleEntity(FetchResults results, GenericEntity entity);
	}
	
	private class EntityFetchHandler extends AbstractEntityFetchHandler {
		private Map<Object, GenericEntity> entities = new HashMap<>();

		public EntityFetchHandler(AbstractEntityGraphNode node, FetchSource source) {
			super(node, source, 0);
		}
		
		@Override
		protected void handleEntity(FetchResults results, GenericEntity entity) {
			entities.put(entity.getId(), entity);
		}
		
		public Map<Object, GenericEntity> entities() {
			return entities;
		}
	}
	
	private abstract class EntityRelatedPropertyFetchHandler extends AbstractEntityFetchHandler {
		protected Map<Object, GenericEntity> owners;
		protected Property property;

		public EntityRelatedPropertyFetchHandler(AbstractEntityGraphNode node, FetchSource source, Map<Object, GenericEntity> owners, Property property) {
			super(node, source, 1);
			this.owners = owners;
			this.property = property;
		}
	}
	
	private class EntityPropertyFetchHandler extends EntityRelatedPropertyFetchHandler {

		public EntityPropertyFetchHandler(AbstractEntityGraphNode node, FetchSource source, Map<Object, GenericEntity> owners, Property property) {
			super(node, source, owners, property);
		}
		
		@Override
		protected void handleEntity(FetchResults results, GenericEntity entity) {
			Object id = results.get(0);
			GenericEntity owner = owners.get(id);
			property.set(owner, entity);
		}
	}
	
	private class EntityCollectionPropertyFetchHandler extends EntityRelatedPropertyFetchHandler {
		private Map<Object, Collection<Object>> collections = new HashMap<>();
		public EntityCollectionPropertyFetchHandler(AbstractEntityGraphNode node, FetchSource source, Map<Object, GenericEntity> owners, Property property) {
			super(node, source, owners, property);
			
			LinearCollectionType type = (LinearCollectionType)property.getType();
			for (GenericEntity entity: owners.values()) {
				Collection<Object> collection = type.createPlain();
				property.setDirect(entity, collection);
				collections.put(entity.getId(), collection);
			}
		}
		
		@Override
		protected void handleEntity(FetchResults results, GenericEntity entity) {
			Object id = results.get(0);
			Collection<Object> collection = collections.get(id);
			collection.add(entity);
		}
	}
	
	private class ScalarCollectionPropertyFetchHandler extends FetchHandler {
		private Map<Object, Collection<Object>> collections = new HashMap<>();
		protected Map<Object, GenericEntity> owners;
		protected Property property;

		public ScalarCollectionPropertyFetchHandler(Map<Object, GenericEntity> owners, Property property) {
			this.owners = owners;
			this.property = property;
			
			LinearCollectionType type = (LinearCollectionType)property.getType();
			for (GenericEntity entity: owners.values()) {
				Collection<Object> collection = type.createPlain();
				property.setDirect(entity, collection);
				collections.put(entity.getId(), collection);
			}
		}
		
		@Override
		public void handleResult(FetchResults results) {
			Object id = results.get(0);
			Object value = results.get(1);
			GenericEntity owner = owners.get(id);
			collections.get(id).add(value);
		}
	}
	
	@Override
	public void fetchEntities(AbstractEntityGraphNode node, Set<Object> ids, Consumer<Map<Object, GenericEntity>> receiver, Consumer<EntityIdm> visitor) {
		FetchQueryFactory queryFactory = queryFactory();
		EntityType<?> baseEntityType = node.entityType();
		FetchQuery query = queryFactory.createQuery(baseEntityType, session().getAccessId());
		FetchSource from = query.fromHydrated();
		
		EntityFetchHandler entityFetchHandler = new EntityFetchHandler(node, from);
		
		fetchBulked(ids, query, entityFetchHandler, () -> {
			receiver.accept(entityFetchHandler.entities());
		});
		
		return ;
	}
	
	private class PropertyQueryPreparation {
		public final FetchQuery query;
		public final FetchSource targetSource;
		
		public PropertyQueryPreparation(EntityGraphNode entityNode, PropertyGraphNode propertyNode) {
			FetchQueryFactory queryFactory = queryFactory();
			EntityType<?> entityType = entityNode.entityType();
			query = queryFactory.createQuery(entityType, session().getAccessId());
			FetchSource from = query.from();
			targetSource = from.join(propertyNode.property());
		}
	}
	
	public void fetchScalarPropertyCollections(EntityGraphNode entityNode, ScalarCollectionPropertyGraphNode propertyNode, Map<Object, GenericEntity> entities, Runnable onDone) {
		PropertyQueryPreparation queryPreparation = new PropertyQueryPreparation(entityNode, propertyNode);
		ScalarCollectionPropertyFetchHandler fetchHandler = new ScalarCollectionPropertyFetchHandler(entities, propertyNode.property());
		fetchBulked(entities.keySet(), queryPreparation.query, fetchHandler, onDone);
	}
	
	@Override
	public void fetchPropertyEntities(EntityGraphNode entityNode, EntityRelatedPropertyGraphNode propertyNode, Map<Object, GenericEntity> entities, Consumer<EntityIdm> visitor, Runnable onDone) {
		PropertyQueryPreparation queryPreparation = new PropertyQueryPreparation(entityNode, propertyNode);
	
		AbstractEntityGraphNode propertyEntityNode = propertyNode.entityNode();
		Property property = propertyNode.property();
		
		final AbstractEntityFetchHandler entityFetchHandler = property.getType().isCollection()? // 
				new EntityCollectionPropertyFetchHandler(propertyEntityNode, queryPreparation.targetSource, entities, property): //
				new EntityPropertyFetchHandler(propertyEntityNode, queryPreparation.targetSource, entities, property);

		entityFetchHandler.setVisitor(visitor);

		fetchBulked(entities.keySet(), queryPreparation.query, entityFetchHandler, onDone);
	}
	
	private void fetchBulked(Set<Object> ids, FetchQuery query, FetchHandler handler, Runnable onDone) {
		List<Set<Object>> idBulks = CollectionTools2.splitToSets(ids, bulkSize());
		
		processParallel(idBulks, bulkIds -> {
			try (FetchResults results = query.fetchFor(bulkIds)) {
				while (results.next()) {
					handler.handleResult(results);
				}
			}
		}, onDone);
	}

	private ExecutorService getExecutorService() {
		if (threadPool != null)
			return threadPool;
		else
			return defaultExecutorLazy.get();
	}

	@Override
	public <T> void processParallel(Collection<T> tasks, Consumer<T> processor, Runnable onDone) {
		if (tasks.isEmpty()) {
			onDone.run();
			return;
		}

		// don't use parallel processing if there is only one task or there is no bulk parallelization
		if (tasks.size() == 1 || !parallelization.isBulkParallel()) {
			try {
				for (T task: tasks) {
					processor.accept(task);
				}
				onDone.run();
			}
			catch (Throwable t) {
				errors.add(t);
			}
		}
		else {
			AttributeContext attributeContext = AttributeContexts.peek();

			Semaphore sem = lazySemaphore.get();

			AtomicInteger countdown = new AtomicInteger();
			ExecutorService executor = getExecutorService();
			for (T task: tasks) {
				notifyParallelTaskComissioned();
				countdown.incrementAndGet();
				executor.submit(() -> {
					try {
						sem.acquire();
						AttributeContexts.with(attributeContext).run(() -> {
							try {
								processor.accept(task);
							}
							catch (Throwable t) {
								errors.add(t);
							}
						});
					}
					catch (InterruptedException e) {
						// ignore
					}
					finally {
						if (countdown.decrementAndGet() == 0) {
							if (errors.isEmpty())
								onDone.run();
						}
						sem.release();
						notifyParallelTaskDone();
					}
				});
			}
		}
	}

	@Override
	public void close() {
		if (defaultExecutorLazy.isInitialized())
			defaultExecutorLazy.close();
	}

	@Override
	public FetchQueryFactory queryFactory() {
		return queryFactory;
	}

	public void setBulkSize(int bulkSize) {
		this.bulkSize = bulkSize;
	}

	public void setMaxParallel(int maxParallel) {
		this.maxParallel = maxParallel;
	}

	public void setJoinProbabilityDefault(double joinProbabilityDefault) {
		this.joinProbabilityDefault = joinProbabilityDefault;
	}

	public void setJoinProbabilityThreshold(double joinProbabilityThreshold) {
		this.joinProbabiltyThreshold = joinProbabilityThreshold;
	}

	public void setToOneScalarThreshold(int toOneScalarThreshold) {
		this.toOneScalarStopThreshold = toOneScalarThreshold;
	}

	public void setParallelization(FetchParallelization parallelization) {
		this.parallelization = parallelization;
	}

	public void setPolymorphicJoin(boolean polymorphicJoin) {
		this.polymorphicJoin = polymorphicJoin;
	}

	public void setToOneJoinThreshold(int toOneJoinThreshold) {
		this.toOneJoinThreshold = toOneJoinThreshold;
	}
}
