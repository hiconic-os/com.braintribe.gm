package dev.hiconic.template.model.core.vd;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface Or extends BooleanJunction {
	EntityType<Or> T = EntityTypes.T(Or.class);
}
