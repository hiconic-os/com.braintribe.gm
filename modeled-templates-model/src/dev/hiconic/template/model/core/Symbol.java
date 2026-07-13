package dev.hiconic.template.model.core;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.meta.PositionalArguments;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

@PositionalArguments("name")
public interface Symbol extends GenericEntity {
	EntityType<Symbol> T = EntityTypes.T(Symbol.class);
	String getName();
	void setName(String name);
}
