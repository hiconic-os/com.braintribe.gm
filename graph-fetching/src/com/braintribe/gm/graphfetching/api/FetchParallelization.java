package com.braintribe.gm.graphfetching.api;

public enum FetchParallelization {
	NONE(false, false), 
	FETCH(true, false), 
	BULK(false, true), 
	FETCH_AND_BULK(true, true);
	
	private boolean bulkParallel;
	private boolean fetchParallel;
	
	private FetchParallelization(boolean fetchParallel, boolean bulkParallel) {
		this.bulkParallel = bulkParallel;
		this.fetchParallel = fetchParallel;
	}
	
	
	public boolean isBulkParallel() {
		return bulkParallel;
	}
	
	public boolean isFetchParallel() {
		return fetchParallel;
	}
}
