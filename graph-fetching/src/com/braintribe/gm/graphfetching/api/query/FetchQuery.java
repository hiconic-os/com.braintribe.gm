package com.braintribe.gm.graphfetching.api.query;

import java.util.Set;

public interface FetchQuery {
	FetchSource from();
	FetchResults fetchFor(Set<Object> entityIds);
	String stringify();
}
