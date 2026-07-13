package dev.hiconic.template.model.core.output;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

import com.braintribe.model.generic.annotation.meta.PositionalArguments;
import dev.hiconic.template.model.core.vd.UnaryOperation;

@PositionalArguments("operand")
public interface XmlEscape extends UnaryOperation {
	EntityType<XmlEscape> T = EntityTypes.T(XmlEscape.class);
}
