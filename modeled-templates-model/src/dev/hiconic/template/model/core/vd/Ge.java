package dev.hiconic.template.model.core.vd;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface Ge extends BinaryBooleanOperator {
	EntityType<Ge> T = EntityTypes.T(Ge.class);
}
