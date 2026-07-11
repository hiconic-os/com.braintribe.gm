package dev.hiconic.template.model.core;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

public interface CommentNode extends TemplateNode {
	EntityType<CommentNode> T = EntityTypes.T(CommentNode.class);

	PropertyLiteral text = PropertyLiteral.of(T, "text");

	String getText();
	void setText(String text);
}
