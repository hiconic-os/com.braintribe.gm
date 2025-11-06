package com.braintribe.gm.graphfetching.test.model;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface Country extends GenericEntity {

	EntityType<Country> T = EntityTypes.T(Country.class);

	String name = "name";
	
	String getName();
	void setName(String name);
}
