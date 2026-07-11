package dev.hiconic.template.model.core;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

import dev.hiconic.template.model.parse.TextRange;

public interface ErrorNode extends TemplateNode {
	EntityType<ErrorNode> T = EntityTypes.T(ErrorNode.class);

	PropertyLiteral text = PropertyLiteral.of(T, "text");
	PropertyLiteral message = PropertyLiteral.of(T, "message");
	PropertyLiteral range = PropertyLiteral.of(T, "range");

	String getText();
	void setText(String text);

	String getMessage();
	void setMessage(String message);

	TextRange getRange();
	void setRange(TextRange range);
}
