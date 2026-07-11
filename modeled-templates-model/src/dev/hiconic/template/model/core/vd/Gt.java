package dev.hiconic.template.model.core.vd;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface Gt extends BinaryBooleanOperator {
	EntityType<Gt> T = EntityTypes.T(Gt.class);
}
