package com.braintribe.gm.graphfetching.api.query;

import java.util.Set;

public interface FetchQuery {
	FetchSource from();
	// resolves the main source as entity with all scalars and TO_ONE properties (using PersistentEntityReference VDs)
	FetchSource fromHydrated();
	FetchResults fetchFor(Set<Object> entityIds);
	String stringify();
}
