package dev.hiconic.template.model.core.output;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

import com.braintribe.model.generic.annotation.meta.PositionalArguments;
import dev.hiconic.template.model.core.vd.UnaryOperation;

@PositionalArguments("operand")
public interface CssEscape extends UnaryOperation {
	EntityType<CssEscape> T = EntityTypes.T(CssEscape.class);
}
