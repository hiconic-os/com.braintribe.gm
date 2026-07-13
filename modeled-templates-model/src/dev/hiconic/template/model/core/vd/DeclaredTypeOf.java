package dev.hiconic.template.model.core.vd;

import com.braintribe.model.generic.annotation.meta.PositionalArguments;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/** Returns the validation-time type recorded for its operand. */
@PositionalArguments("operand")
public interface DeclaredTypeOf extends UnaryOperation {
	EntityType<DeclaredTypeOf> T = EntityTypes.T(DeclaredTypeOf.class);
}
