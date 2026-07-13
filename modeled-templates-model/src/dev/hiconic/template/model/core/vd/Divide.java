package dev.hiconic.template.model.core.vd;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.annotation.meta.PositionalArguments;

@PositionalArguments({"left", "right"})
public interface Divide extends ArithmeticOperation {
	EntityType<Divide> T = EntityTypes.T(Divide.class);
}
