package com.braintribe.gm.graphfetching.api.query;

public interface FetchJoin extends FetchSource {
	void orderByIfRequired();
}
