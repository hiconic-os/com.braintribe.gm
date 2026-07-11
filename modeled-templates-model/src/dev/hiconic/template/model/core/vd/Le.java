package dev.hiconic.template.model.core.vd;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface Le extends BinaryBooleanOperator {
	EntityType<Le> T = EntityTypes.T(Le.class);
}
