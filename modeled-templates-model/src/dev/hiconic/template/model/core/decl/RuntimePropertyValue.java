package dev.hiconic.template.model.core.decl;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

public interface RuntimePropertyValue extends GenericEntity {
	EntityType<RuntimePropertyValue> T = EntityTypes.T(RuntimePropertyValue.class);

	PropertyLiteral specification = PropertyLiteral.of(T, "specification");
	PropertyLiteral value = PropertyLiteral.of(T, "value");

	RuntimePropertySpecification getSpecification();
	void setSpecification(RuntimePropertySpecification specification);

	Object getValue();
	void setValue(Object value);
}
