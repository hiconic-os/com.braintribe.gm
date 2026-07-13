package dev.hiconic.template.model.core.path;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.Transient;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.Property;
import dev.hiconic.template.model.core.Symbol;
import dev.hiconic.template.model.core.TypeReference;

public interface PropertyReference extends GenericEntity {
	EntityType<PropertyReference> T = EntityTypes.T(PropertyReference.class);
	Symbol getSymbol();
	void setSymbol(Symbol symbol);
	TypeReference getDeclaringType();
	void setDeclaringType(TypeReference declaringType);
	TypeReference getType();
	void setType(TypeReference type);
	@Transient Property getResolvedProperty();
	void setResolvedProperty(Property property);
}
