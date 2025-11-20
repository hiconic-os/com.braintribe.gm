package com.braintribe.gm.graphfetching.test.model.data;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

@Abstract
public interface HasName extends GenericEntity{
	EntityType<HasName> T = EntityTypes.T(HasName.class);

	String name = "name";
	
	String getName();
	void setName(String name);
}
