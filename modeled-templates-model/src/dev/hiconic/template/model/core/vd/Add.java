package dev.hiconic.template.model.core.vd;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface Add extends ArithmeticOperation {
	EntityType<Add> T = EntityTypes.T(Add.class);
}
