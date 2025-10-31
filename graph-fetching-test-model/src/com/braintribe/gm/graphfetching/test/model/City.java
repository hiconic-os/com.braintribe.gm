package com.braintribe.gm.graphfetching.test.model;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface City extends GenericEntity {

	EntityType<City> T = EntityTypes.T(City.class);

	String getName();
	void setName(String name);
	
	String getPostalCode();
	void setPostalCode(String postalCode);
}
