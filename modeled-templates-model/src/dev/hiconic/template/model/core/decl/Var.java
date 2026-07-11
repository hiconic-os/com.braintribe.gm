package dev.hiconic.template.model.core.decl;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

public interface Var extends DeclarationNode {
	EntityType<Var> T = EntityTypes.T(Var.class);
	
	PropertyLiteral name = PropertyLiteral.of(T, "name");
	PropertyLiteral type = PropertyLiteral.of(T, "type");
	PropertyLiteral value = PropertyLiteral.of(T, "value");
	
	String getName();
	void setName(String name);
	
	String getType();
	void setType(String type);
	
	Object getValue();
	void setValue(Object value);
}
