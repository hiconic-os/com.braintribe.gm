package dev.hiconic.template.model.core.vd;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface Multiply extends ArithmeticOperation {
	EntityType<Multiply> T = EntityTypes.T(Multiply.class);
}
