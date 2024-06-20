package com.braintribe.model.processing.smood.manipulation;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface MyEntity extends GenericEntity {

	EntityType<MyEntity> T = EntityTypes.T(MyEntity.class);

	String get_name();
	void set_name(String _name);
}
