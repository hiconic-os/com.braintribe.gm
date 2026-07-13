package dev.hiconic.template.model.core.vd;

import com.braintribe.model.generic.annotation.meta.PositionalArguments;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

@PositionalArguments({"left", "right"})
public interface Ge extends BinaryBooleanOperator {
	EntityType<Ge> T = EntityTypes.T(Ge.class);
}
