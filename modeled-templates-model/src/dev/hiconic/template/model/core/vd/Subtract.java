package dev.hiconic.template.model.core.vd;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.annotation.meta.PositionalArguments;

@PositionalArguments({"left", "right"})
public interface Subtract extends ArithmeticOperation {
	EntityType<Subtract> T = EntityTypes.T(Subtract.class);
}
