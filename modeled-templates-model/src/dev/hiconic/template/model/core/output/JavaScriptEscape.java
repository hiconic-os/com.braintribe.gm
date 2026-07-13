package dev.hiconic.template.model.core.output;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

import com.braintribe.model.generic.annotation.meta.PositionalArguments;
import dev.hiconic.template.model.core.vd.UnaryOperation;

@PositionalArguments("operand")
public interface JavaScriptEscape extends UnaryOperation {
	EntityType<JavaScriptEscape> T = EntityTypes.T(JavaScriptEscape.class);
}
