package dev.hiconic.template.model.core.output;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

@Abstract
public interface Transformer extends GenericEntity {
	EntityType<Transformer> T = EntityTypes.T(Transformer.class);
}
