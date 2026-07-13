package dev.hiconic.template.model.core;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.meta.Alias;
import com.braintribe.model.generic.annotation.meta.PositionalArguments;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

@Alias("type")
@Alias("T")
@PositionalArguments("typeSignature")
public interface TypeReference extends GenericEntity {
	EntityType<TypeReference> T = EntityTypes.T(TypeReference.class);
	String getTypeSignature();
	void setTypeSignature(String typeSignature);
}
