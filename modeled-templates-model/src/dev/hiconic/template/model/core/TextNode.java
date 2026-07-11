package dev.hiconic.template.model.core;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

public interface TextNode extends TemplateNode {
	EntityType<TextNode> T = EntityTypes.T(TextNode.class);
	
	PropertyLiteral text = PropertyLiteral.of(T, "text");
	
	String getText();
	void setText(String text);
}
