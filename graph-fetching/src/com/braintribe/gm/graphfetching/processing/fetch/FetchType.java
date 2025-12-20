package com.braintribe.gm.graphfetching.processing.fetch;

public enum FetchType {
	/** entity properties recursive */
	TO_ONE, 
	
	/** collection properties non-recursive */
	TO_MANY,
	
	ALL_FLAT
}