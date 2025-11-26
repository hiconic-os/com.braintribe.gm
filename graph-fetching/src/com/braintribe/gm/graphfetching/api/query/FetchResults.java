package com.braintribe.gm.graphfetching.api.query;

public interface FetchResults extends AutoCloseable {
	boolean next();
	<V> V get(int col);
	
	@Override
	void close();
}
