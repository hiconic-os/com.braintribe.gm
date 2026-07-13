package dev.hiconic.template.model.core.vd;

import com.braintribe.model.generic.annotation.meta.PositionalArguments;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/** Returns the concrete reflected runtime type of its operand. */
@PositionalArguments("operand")
public interface TypeOf extends UnaryOperation {
	EntityType<TypeOf> T = EntityTypes.T(TypeOf.class);
}
