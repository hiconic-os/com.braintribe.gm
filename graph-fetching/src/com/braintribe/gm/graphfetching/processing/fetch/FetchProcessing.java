package com.braintribe.gm.graphfetching.processing.fetch;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import com.braintribe.common.attribute.AttributeContext;
import com.braintribe.common.lcd.Pair;
import com.braintribe.exception.Exceptions;
import com.braintribe.gm.graphfetching.api.node.EntityGraphNode;
import com.braintribe.gm.graphfetching.api.node.FetchQualification;
import com.braintribe.gm.graphfetching.api.node.PropertyGraphNode;
import com.braintribe.logging.Logger;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.utils.collection.impl.AttributeContexts;

/**
 * Implements the orchestration ('processing') for deep entity fetches according to a fetch graph (EntityGraphNode trees). Core idea: Split fetches
 * into tasks (to-one, to-many), schedule/queue them, and process recursively in breadth/depth. Extensible via FetchTask and FetchType.
 */
public class FetchProcessing implements FetchContext {
	protected static final int BULK_SIZE = 100;
	private static Logger logger = Logger.getLogger(FetchProcessing.class);
	private Queue<FetchTask> taskQueue = new LinkedList<>();

	private PersistenceGmSession session;
	private Map<Pair<EntityType<?>, Object>, EntityIdm> index = new HashMap<>();
	private ExecutorService threadPool;

	public FetchProcessing(PersistenceGmSession session) {
		this.session = session;
		threadPool = Executors.newFixedThreadPool(10);
	}

	@Override
	public EntityIdm acquireEntity(GenericEntity entity) {
		return index.computeIfAbsent(Pair.of(entity.entityType(), entity.getId()), k -> new EntityIdm(entity));
	}

	@Override
	public EntityIdm resolveEntity(EntityType<?> type, Object id) {
		return index.get(Pair.of(type, id));
	}

	@Override
	public PersistenceGmSession session() {
		return session;
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
	public void enqueueToOneIfRequired(EntityGraphNode node, Map<Object, GenericEntity> entities) {
		if (node.hasEntityProperties() && !entities.isEmpty()) {
			taskQueue.offer(new FetchTask(node, FetchType.TO_ONE, entities));
		}
	}

	@Override
	public void enqueueToManyIfRequired(EntityGraphNode node, Map<Object, GenericEntity> entities) {
		if (node.hasCollectionProperties() && !entities.isEmpty()) {
			taskQueue.offer(new FetchTask(node, FetchType.TO_MANY, entities));
		}
	}

	private void process() {
		while (true) {
			FetchTask task = taskQueue.poll();

			if (task == null)
				break;

			processTask(task);
		}
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

	private static String getNodeDescription(EntityGraphNode node) {
		if (node instanceof PropertyGraphNode) {
			PropertyGraphNode pgn = (PropertyGraphNode) node;
			return pgn.property().getDeclaringType().getShortName() + "." + pgn.property().getName() + " as " + node.entityType().getShortName();
		}
		return node.entityType().getShortName();
	}

	/**
	 * Handles deep to-one fetching with follow-up to-many fetches on descendants.
	 */
	private void processToOneTask(FetchTask task) {
		ToOneRecursiveFetching toOneFetching = new ToOneRecursiveFetching(task.node);
		toOneFetching.fetch(this, task);
	}

	/**
	 * Handles deep to-many fetching with possible further graph traversal.
	 */
	private void processToManyTask(FetchTask task) {
		ToManyFetching.fetch(this, task.node, task);
	}

	@Override
	public <T> void processParallel(Collection<T> tasks, Consumer<T> processor) {
		AttributeContext attributeContext = AttributeContexts.peek();
		List<Future<?>> futures = new ArrayList<>(tasks.size());
		for (T task : tasks) {
			futures.add(threadPool.submit(() -> {
				AttributeContexts.with(attributeContext).run(() -> {
					processor.accept(task);
				});
			}));
		}
		for (Future<?> future : futures) {
			try {
				future.get();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return;
			} catch (ExecutionException e) {
				try {
					futures.forEach(f -> f.cancel(true));
				} catch (Exception ignore) {
					// Ignore
				}
				throw Exceptions.unchecked(e.getCause());
			}
		}

	}

	@Override
	public void close() {
		if (threadPool != null) {
			threadPool.shutdown();
		}
	}
}
