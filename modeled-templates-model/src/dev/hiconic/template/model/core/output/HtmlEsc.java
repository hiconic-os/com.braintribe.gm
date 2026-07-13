package dev.hiconic.template.model.core.output;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

import com.braintribe.model.generic.annotation.meta.PositionalArguments;
import dev.hiconic.template.model.core.vd.UnaryOperation;

@PositionalArguments("operand")
public interface HtmlEsc extends UnaryOperation {
	EntityType<HtmlEsc> T = EntityTypes.T(HtmlEsc.class);
}
