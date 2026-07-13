package dev.hiconic.template.model.core;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

import dev.hiconic.template.model.core.instr.SilentNode;
import com.braintribe.model.generic.annotation.meta.PositionalArguments;

@PositionalArguments("text")
public interface CommentNode extends TemplateNode, SilentNode {
	EntityType<CommentNode> T = EntityTypes.T(CommentNode.class);

	PropertyLiteral text = PropertyLiteral.of(T, "text");

	SourceText getText();
	void setText(SourceText text);
}
