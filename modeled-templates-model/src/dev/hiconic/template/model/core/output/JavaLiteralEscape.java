package dev.hiconic.template.model.core.output;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

import com.braintribe.model.generic.annotation.meta.PositionalArguments;
import dev.hiconic.template.model.core.vd.UnaryOperation;

@PositionalArguments("operand")
public interface JavaLiteralEscape extends UnaryOperation {
	EntityType<JavaLiteralEscape> T = EntityTypes.T(JavaLiteralEscape.class);
}
