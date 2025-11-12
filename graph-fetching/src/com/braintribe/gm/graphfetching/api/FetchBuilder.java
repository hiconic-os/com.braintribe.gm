package com.braintribe.gm.graphfetching.api;

import java.util.Collection;
import java.util.List;

import com.braintribe.gm.graphfetching.api.query.FetchQueryFactory;
import com.braintribe.model.generic.GenericEntity;

public interface FetchBuilder {
	FetchBuilder queryFactory(FetchQueryFactory queryFactory);
	
	<E extends GenericEntity> List<E> fetchDetached(Collection<? extends E> entities);
	
	void fetch(Collection<? extends GenericEntity> entities);}
