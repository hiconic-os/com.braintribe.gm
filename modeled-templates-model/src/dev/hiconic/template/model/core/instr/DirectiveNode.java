package dev.hiconic.template.model.core.instr;

import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

import dev.hiconic.template.model.core.TemplateNode;

/** A model-backed directive that may occur in the {@code %(Type ...)} form. */
@Abstract
public interface DirectiveNode extends TemplateNode {
	EntityType<DirectiveNode> T = EntityTypes.T(DirectiveNode.class);
	PropertyLiteral whitespace = PropertyLiteral.of(T, "whitespace");

	WhitespacePolicy getWhitespace();
	void setWhitespace(WhitespacePolicy whitespace);
}
