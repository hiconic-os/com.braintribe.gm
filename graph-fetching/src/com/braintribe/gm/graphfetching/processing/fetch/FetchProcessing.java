package com.braintribe.gm.graphfetching.processing.fetch;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
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
import com.braintribe.exception.CanceledException;
import com.braintribe.exception.Exceptions;
import com.braintribe.gm.graphfetching.api.FetchParallelization;
import com.braintribe.gm.graphfetching.api.node.AbstractEntityGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityGraphNode;
import com.braintribe.gm.graphfetching.api.node.FetchQualification;
import com.braintribe.gm.graphfetching.api.query.FetchQueryFactory;
import com.braintribe.gm.graphfetching.processing.query.GmSessionFetchQueryFactory;
import com.braintribe.gm.graphfetching.processing.util.FetchingTools;
import com.braintribe.logging.Logger;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.utils.collection.impl.AttributeContexts;
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
	private boolean multithreaded = true;
	private FetchParallelization parallelization;
	private boolean polymorphicJoin = true;

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
	public boolean polymorphicJoin() {
		return polymorphicJoin;
	}

	@Override
	public EntityIdm acquireEntity(GenericEntity entity) {
		return index.computeIfAbsent(Pair.of(entity.entityType(), entity.getId()), k -> {
			if (entity.isEnhanced())
				return new EntityIdm(entity);

			return new EntityIdm(FetchingTools.cloneDetachment(entity));
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
			parallelTaskCounter.incrementAndGet();

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
			System.out.println("Running task, sem: " + semaphore.availablePermits());
			processTask(task);
		} finally {
			semaphore.release();

			if (parallelTaskCounter.decrementAndGet() == 0) {
				parallelDoneMonitor.lock();

				try {
					parallelDoneCondition.signal();
				} finally {
					parallelDoneMonitor.unlock();
				}
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

		List<Exception> exceptions = new ArrayList<>();
		for (Future<?> future : futures) {
			try {
				future.get();
			} catch (Exception e) {
				exceptions.add(e);
			}
		}

		if (exceptions.isEmpty())
			return;

		RuntimeException e = new RuntimeException("Error while executing parallel fetch tasks");

		if (exceptions.size() == 1)
			e.initCause(exceptions.get(0));
		else {
			for (Exception cause : exceptions) {
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

	private ExecutorService getExecutorService() {
		if (threadPool != null)
			return threadPool;
		else
			return defaultExecutorLazy.get();
	}

	@Override
	public <T> void processParallel(Collection<T> tasks, Consumer<T> processor) {
		if (tasks.isEmpty())
			return;

		// don't use parallel processing if there is only one task or there is no bulk parallelization
		if (tasks.size() == 1) {
			processor.accept(tasks.iterator().next());
			return;
		}

		Iterator<T> iterator = tasks.iterator();
		final List<Future<?>> futures;

		if (parallelization.isBulkParallel()) {
			int parallelSize = tasks.size() - 1;
			futures = new ArrayList<>(parallelSize);

			AttributeContext attributeContext = AttributeContexts.peek();

			Semaphore sem = lazySemaphore.get();

			ExecutorService executor = getExecutorService();
			for (int i = 0; i < parallelSize; i++) {
				T task = iterator.next();
				sem.acquireUninterruptibly();
				futures.add(executor.submit(() -> {
					try {
						AttributeContexts.with(attributeContext).run(() -> {
							processor.accept(task);
						});
					} finally {
						sem.release();
					}
				}));
			}
		} else {
			futures = Collections.emptyList();
		}

		// synchronous part
		while (iterator.hasNext()) {
			T task = iterator.next();

			CompletableFuture<Void> future = new CompletableFuture<>();

			try {
				processor.accept(task);
				future.complete(null);
			} catch (Exception e) {
				future.completeExceptionally(e);
			}

			futures.add(future);
		}

		for (Future<?> future : futures) {
			try {
				future.get();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new CanceledException(e);
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
}
