package dev.hiconic.template.model.core.vd;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface And extends BooleanJunction {
	EntityType<And> T = EntityTypes.T(And.class);
}
