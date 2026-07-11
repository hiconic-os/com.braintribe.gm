package dev.hiconic.template.model.core.vd;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface Eq extends BinaryBooleanOperator {
	EntityType<Eq> T = EntityTypes.T(Eq.class);
}
