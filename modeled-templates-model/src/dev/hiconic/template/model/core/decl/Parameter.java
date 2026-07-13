package dev.hiconic.template.model.core.decl;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

@Deprecated
public interface Parameter extends GenericEntity {
	EntityType<Parameter> T = EntityTypes.T(Parameter.class);
	
	PropertyLiteral name = PropertyLiteral.of(T, "name");
	PropertyLiteral type = PropertyLiteral.of(T, "type");
	
	String getName();
	void setName(String name);
	
	String getType();
	void setType(String type);
}
