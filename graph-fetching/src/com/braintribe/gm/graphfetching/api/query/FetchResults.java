package com.braintribe.gm.graphfetching.api.query;

public interface FetchResults {
	boolean next();
	<V> V get(int col);
}
