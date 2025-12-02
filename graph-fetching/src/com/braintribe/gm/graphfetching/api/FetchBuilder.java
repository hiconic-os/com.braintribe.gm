package com.braintribe.gm.graphfetching.api;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;

import com.braintribe.gm.graphfetching.api.query.FetchQueryFactory;
import com.braintribe.model.generic.GenericEntity;

public interface FetchBuilder {
	FetchBuilder queryFactory(FetchQueryFactory queryFactory);
	FetchBuilder executor(ExecutorService executor);
	FetchBuilder bulkSize(int bulkSize);
	FetchBuilder toOneScalarThreshold(int threshold);
	FetchBuilder joinProbabilityThreshold(double threshold);
	FetchBuilder joinProbabilityDefault(double probability);
	FetchBuilder maxParallel(int maxParallel);
	FetchBuilder parallelization(FetchParallelization parallelization);
	FetchBuilder polymorphicJoin(boolean polymorphicJoin);
	
	<E extends GenericEntity> List<E> fetchDetached(Collection<? extends E> entities);
	
	void fetch(Collection<? extends GenericEntity> entities);}
