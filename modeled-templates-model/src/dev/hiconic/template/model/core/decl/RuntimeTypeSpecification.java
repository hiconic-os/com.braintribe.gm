package dev.hiconic.template.model.core.decl;

import java.util.List;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

public interface RuntimeTypeSpecification extends GenericEntity {
	EntityType<RuntimeTypeSpecification> T = EntityTypes.T(RuntimeTypeSpecification.class);

	PropertyLiteral name = PropertyLiteral.of(T, "name");
	PropertyLiteral properties = PropertyLiteral.of(T, "properties");

	String getName();
	void setName(String name);

	List<RuntimePropertySpecification> getProperties();
	void setProperties(List<RuntimePropertySpecification> properties);
}
