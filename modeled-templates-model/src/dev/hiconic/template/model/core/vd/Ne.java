package dev.hiconic.template.model.core.vd;

import com.braintribe.model.generic.annotation.meta.PositionalArguments;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

@PositionalArguments({"left", "right"})
public interface Ne extends BinaryBooleanOperator {
	EntityType<Ne> T = EntityTypes.T(Ne.class);
}
