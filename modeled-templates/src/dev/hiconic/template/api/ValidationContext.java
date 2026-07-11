package dev.hiconic.template.api;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.PropertyLiteral;

public interface ValidationContext {
	GenericModelType getType(GenericEntity entity, Property property);

	default GenericModelType getType(GenericEntity entity, PropertyLiteral property) {
		return getType(entity, property.property());
	}

	default GenericModelType resolveType(String typeName) {
		return GMF.getTypeReflection().findType(typeName);
	}
}
