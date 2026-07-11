package dev.hiconic.template.model.core.decl;

import java.util.List;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

public interface RuntimeArguments extends GenericEntity {
	EntityType<RuntimeArguments> T = EntityTypes.T(RuntimeArguments.class);

	PropertyLiteral typeSpecification = PropertyLiteral.of(T, "typeSpecification");
	PropertyLiteral values = PropertyLiteral.of(T, "values");

	RuntimeTypeSpecification getTypeSpecification();
	void setTypeSpecification(RuntimeTypeSpecification typeSpecification);

	List<RuntimePropertyValue> getValues();
	void setValues(List<RuntimePropertyValue> values);
}
