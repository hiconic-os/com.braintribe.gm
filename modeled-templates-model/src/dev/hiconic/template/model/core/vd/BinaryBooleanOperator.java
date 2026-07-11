package dev.hiconic.template.model.core.vd;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.value.type.BooleanDescriptor;

public interface BinaryBooleanOperator extends BinaryOperator, BooleanDescriptor {
	EntityType<BinaryBooleanOperator> T = EntityTypes.T(BinaryBooleanOperator.class);
}
