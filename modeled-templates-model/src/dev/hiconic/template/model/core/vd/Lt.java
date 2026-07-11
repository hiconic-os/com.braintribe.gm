package dev.hiconic.template.model.core.vd;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface Lt extends BinaryBooleanOperator {
	EntityType<Lt> T = EntityTypes.T(Lt.class);
}
