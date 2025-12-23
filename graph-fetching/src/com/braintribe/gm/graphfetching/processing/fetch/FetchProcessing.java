package com.braintribe.gm.graphfetching.processing.fetch;

import static com.braintribe.utils.lcd.CollectionTools2.newList;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.braintribe.common.attribute.AttributeContext;
import com.braintribe.common.lcd.Pair;
import com.braintribe.gm.graphfetching.api.FetchParallelization;
import com.braintribe.gm.graphfetching.api.node.AbstractEntityGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityRelatedPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.FetchQualification;
import com.braintribe.gm.graphfetching.api.node.MapPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.PropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.ScalarCollectionPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.query.FetchJoin;
import com.braintribe.gm.graphfetching.api.query.FetchQuery;
import com.braintribe.gm.graphfetching.api.query.FetchQueryFactory;
import com.braintribe.gm.graphfetching.api.query.FetchQueryOptions;
import com.braintribe.gm.graphfetching.api.query.FetchResults;
import com.braintribe.gm.graphfetching.api.query.FetchSource;
import com.braintribe.gm.graphfetching.processing.query.GmSessionFetchQueryFactory;
import com.braintribe.logging.Logger;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.LinearCollectionType;
import com.braintribe.model.generic.reflection.MapType;
import com.braintribe.model.generic.reflection.Property;
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
		
		boolean flatFetching = toOneJoinThreshold == 0;
		
		if (flatFetching) {
			FetchQualification fqFlat = new FetchQualification(node, FetchType.ALL_FLAT);
			for (GenericEntity entity : entities.values()) {
				EntityIdm idm = acquireEntity(entity);
				idm.addHandled(fqFlat);
			}
			enqueueFlatIfRequired(node, entities);
		}
		else {
			FetchQualification fqToOne = new FetchQualification(node, FetchType.TO_ONE);
			FetchQualification fqToMany = new FetchQualification(node, FetchType.TO_MANY);
			for (GenericEntity entity : entities.values()) {
				EntityIdm idm = acquireEntity(entity);
				idm.addHandled(fqToOne);
				idm.addHandled(fqToMany);
			}
			enqueueToOneIfRequired(node, entities);
			enqueueToManyIfRequired(node, entities);
		}
		
		process();
		
		Duration duration = Duration.ofNanos(System.nanoTime() - nanosStart);
		logger.debug(() -> "consumed " + duration.toMillis() + " ms for graph " + (flatFetching? "flat ": "") + "fetching of " + index.size() + " entities of node "
				+ getNodeDescription(node) + " " + indexAsString());
	}
	
	private String indexAsString() {
		StringBuilder builder = new StringBuilder();
		
		for (EntityIdm idm: index.values()) {
			if (builder.length() > 0)
				builder.append(", ");
			GenericEntity entity = idm.entity;
			builder.append(entity.entityType().getShortName() + "@" + entity.getId());
		}
		
		return builder.toString();
	}
	
	@Override
	public void notifyError(Throwable ex) {
		errors.add(ex);
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
	
	@Override
	public void enqueueFlatIfRequired(AbstractEntityGraphNode node, Map<Object, GenericEntity> entities) {
		if (node.hasCollectionProperties() || node.hasEntityProperties() && !entities.isEmpty()) {
			enqueue(new FetchTask(node, FetchType.ALL_FLAT, entities));
		}
	}

	@Override
	public void enqueue(FetchTask task) {
		if (parallelization.isFetchParallel()) {
			AttributeContext attributeContext = AttributeContexts.peek();
			
			CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
				AttributeContexts.with(attributeContext).run(() -> {
					processSemaphored(task);
				});
			}, defaultExecutorLazy.get());
			
			observe(future);
		} else
			taskQueue.offer(task);
	}

	private void processSemaphored(FetchTask task) {
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
		}
	}

	@Override
	public void observe(CompletableFuture<?> future) {
		notifyParallelTaskComissioned();
		future.whenComplete((r,ex) -> {
			if (ex != null)
				errors.add(ex);
			notifyParallelTaskDone();
		});
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

		final CompletableFuture<?> future;
		
		switch (task.fetchType) {
			case TO_MANY:
				future = processToManyTask(task);
				break;
			case TO_ONE:
				future = processToOneTask(task);
				break;
			case ALL_FLAT:
				future = processFlatTask(task);
				break;
			default:
				throw new IllegalStateException("Unexpected fetch type: " + task.fetchType);
		}
		
		observe(future.whenComplete((r, ex) -> {
			Duration duration = Duration.ofNanos(System.nanoTime() - nanosStart);
			logger.trace(() -> "consumed " + duration.toMillis() + " ms for graph " + task.fetchType + " fetching of " + task.entities.size()
					+ " entities of node: " + getNodeDescription(task.node));
		}));
	}

	private static String getNodeDescription(AbstractEntityGraphNode node) {
		return node.entityType().getShortName();
	}

	/**
	 * Handles deep to-one fetching with follow-up to-many fetches on descendants.
	 */
	private CompletableFuture<?> processToOneTask(FetchTask task) {
		ToOneRecursiveFetching toOneFetching = new ToOneRecursiveFetching(this, task.node);
		return toOneFetching.fetch(this, task);
	}
	
	private CompletableFuture<?> processFlatTask(FetchTask task) {
		FlatFetching flatFetching = new FlatFetching(this, task);
		return flatFetching.fetch();
	}

	/**
	 * Handles deep to-many fetching with possible further graph traversal.
	 */
	private CompletableFuture<?> processToManyTask(FetchTask task) {
		return ToManyFetching.fetch(this, task.node, task);
	}
	
	private interface FetchHandler {
		void handleResult(FetchResults results);
	}
	
	private interface ValueExtracter {
		public Object extract(FetchResults results);
	}
	
	private class ScalarExtracter implements ValueExtracter {
		private int pos;
		
		public ScalarExtracter(int pos) {
			super();
			this.pos = pos;
		}

		@Override
		public Object extract(FetchResults results) {
			return results.get(pos);
		}
	}
	
	private class EntityExtracter implements ValueExtracter {
		private int pos;
		private EntityType<?> baseEntityType;
		private Consumer<EntityIdm> visitor;
		
		public EntityExtracter(AbstractEntityGraphNode node, int pos) {
			this.pos = pos;
			baseEntityType = node.entityType();
		}
		
		public EntityExtracter(AbstractEntityGraphNode node, int pos, Consumer<EntityIdm> visitor) {
			this.pos = pos;
			this.visitor = visitor;
			baseEntityType = node.entityType();
		}
		
		public GenericEntity extract(FetchResults results) {
			int i = pos;
			
			GenericEntity entity = results.get(i++);
			
			if (entity == null)
				return null;
			
			EntityIdm entityIdm = acquireEntity(entity);
			
			GenericEntity effectiveEntity = entityIdm.entity;
			
			if (baseEntityType != effectiveEntity.entityType())
				putEntity(baseEntityType, entityIdm);
			
			if (visitor != null)
				visitor.accept(entityIdm);
			
			return entityIdm.entity;
		}
	}
	
	private class EntityFetchHandler extends EntityExtracter implements FetchHandler {
		private Map<Object, GenericEntity> entities = new ConcurrentHashMap<>();

		public EntityFetchHandler(AbstractEntityGraphNode node) {
			super(node, 0);
		}
		
		public EntityFetchHandler(AbstractEntityGraphNode node, FetchSource source, int pos) {
			super(node, pos);
		}
		
		@Override
		public void handleResult(FetchResults results) {
			GenericEntity entity = extract(results);
			
			if (entity == null)
				return;
			
			entities.put(entity.getId(), entity);
		}
		
		public Map<Object, GenericEntity> entities() {
			return entities;
		}
	}
	
	private class ToOnePropertyFetchHandler implements FetchHandler {
		
		private ValueExtracter valueExtracter;
		private Map<Object, GenericEntity> owners;
		private Property property;

		public ToOnePropertyFetchHandler(ValueExtracter valueExtracter, Map<Object, GenericEntity> owners, Property property) {
			this.valueExtracter = valueExtracter;
			this.owners = owners;
			this.property = property;
		}

		@Override
		public void handleResult(FetchResults results) {
			Object id = results.get(0);
			Object value = valueExtracter.extract(results);
			GenericEntity owner = owners.get(id);
			property.set(owner, value);
		}
		
	}
	
	private class MapPropertyFetchHandler implements FetchHandler {
		private Map<Object, Map<Object, Object>> maps = new HashMap<>();
		private ValueExtracter keyExtracter;
		private ValueExtracter valueExtracter;

		public MapPropertyFetchHandler(ValueExtracter keyExtracter, ValueExtracter valueExtracter, Map<Object, GenericEntity> owners, Property property) {
			this.keyExtracter = keyExtracter;
			this.valueExtracter = valueExtracter;
			
			MapType type = (MapType)property.getType();
			for (GenericEntity entity: owners.values()) {
				Map<Object, Object> map = type.createPlain();
				property.setDirect(entity, map);
				maps.put(entity.getId(), map);
			}
		}
		
		@Override
		public void handleResult(FetchResults results) {
			final Object key = keyExtracter.extract(results);
			final Object value = valueExtracter.extract(results);
			
			Object id = results.get(0);
			maps.get(id).put(key, value);
		}
	}
	
	private class CollectionPropertyFetchHandler implements FetchHandler {
		private Map<Object, Collection<Object>> collections = new HashMap<>();
		private ValueExtracter elementExtracter;
		
		public CollectionPropertyFetchHandler(ValueExtracter elementExtracter, Map<Object, GenericEntity> owners, Property property) {
			this.elementExtracter = elementExtracter;
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
			Object value = elementExtracter.extract(results);
			collections.get(id).add(value);
		}
	}
	
	@Override
	public CompletableFuture<Map<Object, GenericEntity>> fetchEntities(AbstractEntityGraphNode node, Set<Object> ids, Consumer<EntityIdm> visitor) {
		FetchQueryFactory queryFactory = queryFactory();
		EntityType<?> baseEntityType = node.entityType();
		FetchQueryOptions options = new FetchQueryOptions();
		options.setHydrateFrom(true);
		options.setHydrateAbsentEntitiesIfPossible(true);
		FetchQuery query = queryFactory.createQuery(baseEntityType, session().getAccessId(), options);
		query.from();
		
		EntityFetchHandler entityFetchHandler = new EntityFetchHandler(node);
		
		CompletableFuture<Map<Object, GenericEntity>> future = new CompletableFuture<>();
		
		Predicate<Object> existingEntitiesHandler = id -> {
			EntityIdm entityIdm = resolveEntity(baseEntityType, id);
			if (entityIdm == null)
				return false;
			
			System.out.println("known entity found");
			entityFetchHandler.entities.put(id, entityIdm.entity);
			visitor.accept(entityIdm);
			
			return true;
		};
		
		fetchBulked(ids, query, entityFetchHandler, existingEntitiesHandler) //
			.whenComplete((r,ex) -> {
				if (ex != null)
					future.completeExceptionally(ex);
				else
					future.complete(entityFetchHandler.entities());
			});
		
		return future;
	}
	
	private class PropertyQueryPreparation {
		public final FetchQuery query;
		public final FetchJoin targetSource;
		
		public PropertyQueryPreparation(EntityGraphNode entityNode, PropertyGraphNode propertyNode) {
			FetchQueryFactory queryFactory = queryFactory();
			EntityType<?> entityType = entityNode.entityType();
			FetchQueryOptions options = new FetchQueryOptions();
			options.setHydrateAbsentEntitiesIfPossible(true);
			query = queryFactory.createQuery(entityType, session().getAccessId());
			FetchSource from = query.from();
			targetSource = from.join(propertyNode.property());
			targetSource.orderByIfRequired();
		}
	}
	
	@Override
	public CompletableFuture<Void> fetchScalarPropertyCollections(EntityGraphNode entityNode, ScalarCollectionPropertyGraphNode propertyNode, Map<Object, GenericEntity> entities) {
		PropertyQueryPreparation queryPreparation = new PropertyQueryPreparation(entityNode, propertyNode);

		FetchHandler fetchHandler = new CollectionPropertyFetchHandler(new ScalarExtracter(1), entities, propertyNode.property());
		return fetchBulked(entities.keySet(), queryPreparation.query, fetchHandler);
	}
	
	@Override
	public CompletableFuture<Void> fetchPropertyEntities(EntityGraphNode entityNode, EntityRelatedPropertyGraphNode propertyNode, Map<Object, GenericEntity> entities, Consumer<EntityIdm> visitor) {
		PropertyQueryPreparation queryPreparation = new PropertyQueryPreparation(entityNode, propertyNode);
	
		AbstractEntityGraphNode propertyEntityNode = propertyNode.entityNode();
		Property property = propertyNode.property();
		
		EntityExtracter entityExtracter = new EntityExtracter(propertyEntityNode, 1, visitor);
		
		final FetchHandler entityFetchHandler = property.getType().isCollection()? // 
				new CollectionPropertyFetchHandler(entityExtracter, entities, property): //
				new ToOnePropertyFetchHandler(entityExtracter, entities, property);

		return fetchBulked(entities.keySet(), queryPreparation.query, entityFetchHandler);
	}
	
	@Override
	public CompletableFuture<Void> fetchMap(EntityGraphNode entityNode, MapPropertyGraphNode propertyNode, Map<Object, GenericEntity> entities, Consumer<EntityIdm> keyVisitor, Consumer<EntityIdm> valueVisitor) {
		PropertyQueryPreparation queryPreparation = new PropertyQueryPreparation(entityNode, propertyNode);
		
		final ValueExtracter keyExtracter;
		final ValueExtracter valueExtracter;
		
		AbstractEntityGraphNode keyNode = propertyNode.keyNode();
		AbstractEntityGraphNode valueNode = propertyNode.valueNode();
		
		if (keyNode != null) {
			keyExtracter = new EntityExtracter(keyNode, 1, keyVisitor);
		}
		else {
			keyExtracter = new ScalarExtracter(1);
		}
		
		if (valueNode != null) {
			valueExtracter = new EntityExtracter(valueNode, 2, valueVisitor);
		}
		else {
			valueExtracter = new ScalarExtracter(2);
		}
		
		FetchHandler fetchHandler = new MapPropertyFetchHandler(keyExtracter, valueExtracter, entities, propertyNode.property());
		
		return fetchBulked(entities.keySet(), queryPreparation.query, fetchHandler);
	}
	
	public static <T> List<Set<T>> split(Iterable<? extends T> elements, int limitSize, Predicate<T> exclusion) {
		List<Set<T>> result = newList();

		int partSize = limitSize;
		Set<T> part = null;

		for (T element : elements) {
			if (exclusion.test(element))
				continue;
			
			if (partSize == limitSize) {
				partSize = 0;
				result.add(part = new HashSet<>());
			}

			part.add(element);
			partSize++;
		}

		return result;
	}
	
	private CompletableFuture<Void> fetchBulked(Collection<Object> ids, FetchQuery query, FetchHandler handler) {
		return fetchBulked(ids, query, handler, id -> false);
	}
	
	private CompletableFuture<Void> fetchBulked(Collection<Object> ids, FetchQuery query, FetchHandler handler, Predicate<Object> exclusion) {
		List<Set<Object>> idBulks = split(ids, bulkSize(), exclusion);
		
		return processElements(idBulks, bulkIds -> {
			long nanosStart = System.nanoTime();
			try (FetchResults results = query.fetchFor(bulkIds)) {
				while (results.next()) {
					handler.handleResult(results);
				}
			}
			
			long queryDuration = System.nanoTime() - nanosStart;

			logger.trace(() ->"Query took " + Duration.ofNanos(queryDuration).toMillis() + " ms for querying " + bulkIds.size() + " entities: " + query.stringify());
		});
	}

	private ExecutorService getExecutorService() {
		if (threadPool != null)
			return threadPool;
		else
			return defaultExecutorLazy.get();
	}

	@Override
	public <T> CompletableFuture<Void> processElements(Collection<T> tasks, Consumer<T> processor) {
		if (tasks.isEmpty()) {
			return CompletableFuture.completedFuture(null);
		}
		
		// don't use parallel processing if there is only one task or there is no bulk parallelization
		if (tasks.size() == 1 || !parallelization.isBulkParallel()) {
			
			try {
				for (T task: tasks) {
					processor.accept(task);
				}
				return CompletableFuture.completedFuture(null);
			}
			catch (Throwable t) {
				CompletableFuture<Void> future = new CompletableFuture<>();
				future.completeExceptionally(t);
				return future;
			}
		}
		else {
			AttributeContext attributeContext = AttributeContexts.peek();
			
			List<CompletableFuture<Void>> futures = new ArrayList<>(tasks.size());
			
			Semaphore sem = lazySemaphore.get();
			
			ExecutorService executor = getExecutorService();
			for (T task: tasks) {
				CompletableFuture<Void> taskFuture = CompletableFuture.runAsync(() -> {
					try {
						sem.acquire();
						try {
							AttributeContexts.with(attributeContext).run(() -> {
								processor.accept(task);
							});
						}
						finally {
							sem.release();
						}
					}
					catch (InterruptedException e) {
						// ignore
						Thread.currentThread().interrupt();
					}
				}, executor);
				
				futures.add(taskFuture);
			}
			return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
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
