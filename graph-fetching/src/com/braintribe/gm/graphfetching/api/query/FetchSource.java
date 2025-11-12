package com.braintribe.gm.graphfetching.api.query;

import com.braintribe.model.generic.reflection.Property;

public interface FetchSource {
	int pos();
	FetchJoin leftJoin(Property property);
	FetchJoin join(Property property);
}
