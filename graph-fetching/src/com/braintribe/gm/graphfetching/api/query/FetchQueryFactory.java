package com.braintribe.gm.graphfetching.api.query;

import com.braintribe.model.generic.reflection.EntityType;

public interface FetchQueryFactory {
	FetchQuery createQuery(EntityType<?> entityType, String defaultPartition);
	
	default FetchQuery createQueryToOneOnly(EntityType<?> entityType, String defaultPartition) {
		return createQuery(entityType, defaultPartition);
	}
	
	boolean supportsSubTypeJoin();
}
