package com.braintribe.gm.graphfetching.processing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;

import com.braintribe.gm.graphfetching.api.FetchBuilder;
import com.braintribe.gm.graphfetching.api.FetchParallelization;
import com.braintribe.gm.graphfetching.api.node.EntityGraphNode;
import com.braintribe.gm.graphfetching.api.query.FetchQueryFactory;
import com.braintribe.gm.graphfetching.processing.fetch.FetchProcessing;
import com.braintribe.gm.graphfetching.processing.util.FetchingTools;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;

public class FetchBuilderImpl implements FetchBuilder {
	private PersistenceGmSession session;
	private EntityGraphNode node;
	private FetchQueryFactory queryFactory;
	private ExecutorService executor;
	private int bulkSize = 100;
	private int maxParallel = 10;
	private double joinProbabilityThreshold = 0.05;
	private double joinProbabilityDefault = 0.5;
	private int toOneScalarThreshold = 500;
	private FetchParallelization parallelization = FetchParallelization.FETCH_AND_BULK;
	private boolean polymorphicJoin = false;
	
	public FetchBuilderImpl(PersistenceGmSession session, EntityGraphNode node) {
		super();
		this.session = session;
		this.node = node;
	}
	@Override
	public FetchBuilder queryFactory(FetchQueryFactory queryFactory) {
		this.queryFactory = queryFactory;
		return this;
	}
	
	@Override
	public FetchBuilder bulkSize(int bulkSize) {
		this.bulkSize  = bulkSize;
		return this;
	}
	
	@Override
	public FetchBuilder toOneScalarThreshold(int threshold) {
		toOneScalarThreshold = threshold;
		return this;
	}
	
	@Override
	public FetchBuilder joinProbabilityDefault(double probability) {
		joinProbabilityDefault = probability;
		return this;
	}
	
	@Override
	public FetchBuilder joinProbabilityThreshold(double threshold) {
		joinProbabilityThreshold = threshold;
		return this;
	}
	
	@Override
	public FetchBuilder maxParallel(int maxParallel) {
		this.maxParallel  = maxParallel;
		return this;
	}
	
	@Override
	public FetchBuilder parallelization(FetchParallelization parallelization) {
		this.parallelization = parallelization;
		return this;
	}
	
	@Override
	public FetchBuilder polymorphicJoin(boolean polymorphicJoin) {
		this.polymorphicJoin = polymorphicJoin;
		return this;
	}
	
	@Override
	public FetchBuilder executor(ExecutorService executor) {
		this.executor = executor;
		return this;
	}
	
	@Override
	public <E extends GenericEntity> List<E> fetchDetached(Collection<? extends E> entities) {
		List<E> detachedEntities = new ArrayList<>();

		for (E entity : entities) {
			E detachedEntity = FetchingTools.cloneDetachment(entity);
			detachedEntities.add(detachedEntity);
		}

		try (FetchProcessing processing = fetchProcessing()) {
			processing.fetch(node, detachedEntities);

			return detachedEntities;
		}
	}
	
	@Override
	public void fetch(Collection<? extends GenericEntity> entities) {
		if (entities.isEmpty())
			return;

		GenericEntity entity = entities.iterator().next();

		if (entity.session() == session) {
			List<GenericEntity> detachedEntities = fetchDetached(entities);
			session.merge().adoptUnexposed(true).suspendHistory(true).doFor(detachedEntities);
		} else {
			try (FetchProcessing processing = fetchProcessing()) {
				processing.fetch(node, entities);
			}
		}
	}
	
	private FetchProcessing fetchProcessing() {
		final FetchProcessing fetchProcessing;
		if (queryFactory != null)
			fetchProcessing = new FetchProcessing(session, queryFactory);
		else
			fetchProcessing = new FetchProcessing(session);
		
		if (executor != null)
			fetchProcessing.setExecutorService(executor);
		
		fetchProcessing.setBulkSize(bulkSize);
		fetchProcessing.setMaxParallel(maxParallel);
		fetchProcessing.setJoinProbabilityDefault(joinProbabilityDefault);
		fetchProcessing.setJoinProbabilityThreshold(joinProbabilityThreshold);
		fetchProcessing.setToOneScalarThreshold(toOneScalarThreshold);
		fetchProcessing.setParallelization(parallelization);
		fetchProcessing.setPolymorphicJoin(polymorphicJoin);
		
		return fetchProcessing;
			
	}
}
