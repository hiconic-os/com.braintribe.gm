package dev.hiconic.template.model.core.vd;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface Subtract extends ArithmeticOperation {
	EntityType<Subtract> T = EntityTypes.T(Subtract.class);
}
