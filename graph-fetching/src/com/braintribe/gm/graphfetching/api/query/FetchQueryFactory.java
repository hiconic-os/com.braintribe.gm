package com.braintribe.gm.graphfetching.api.query;

import com.braintribe.model.generic.reflection.EntityType;

public interface FetchQueryFactory {
	default FetchQuery createQuery(EntityType<?> entityType, String defaultPartition) {
		return createQuery(entityType, defaultPartition, FetchQueryOptions.DEFAULTS);
	}
	FetchQuery createQuery(EntityType<?> entityType, String defaultPartition, FetchQueryOptions options);
	boolean supportsSubTypeJoin();
}
