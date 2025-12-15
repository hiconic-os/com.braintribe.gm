package com.braintribe.gm.graphfetching.api.query;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.Property;

public interface FetchSource {
	int pos();
	int selectEntityId(Property property);
	int selectEntityId(String propertyName);

	FetchJoin leftJoin(Property property);
	FetchJoin join(Property property);
	int scalarCount();
	
	/** 
	 * Casts the this source to a polymorphic subtype if the querying supports this feature otherwise null 
	 */
	default FetchSource as(EntityType<?> entityType) {
		return null;
	}
}
