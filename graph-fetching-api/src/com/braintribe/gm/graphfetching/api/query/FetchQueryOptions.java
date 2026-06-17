package com.braintribe.gm.graphfetching.api.query;

public class FetchQueryOptions {
	public static final FetchQueryOptions DEFAULTS = new FetchQueryOptions(); 
	
	private boolean hydrateFrom;
	private boolean hydrateAbsentEntitiesIfPossible;
	
	public boolean getHydrateFrom() {
		return hydrateFrom;
	}
	public void setHydrateFrom(boolean hydrateFrom) {
		this.hydrateFrom = hydrateFrom;
	}
	
	public boolean getHydrateAbsentEntitiesIfPossible() {
		return hydrateAbsentEntitiesIfPossible;
	}
	
	public void setHydrateAbsentEntitiesIfPossible(boolean hydrateAbsentEntitiesIfPossible) {
		this.hydrateAbsentEntitiesIfPossible = hydrateAbsentEntitiesIfPossible;
	}
}
