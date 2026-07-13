package dev.hiconic.template.model.parse;

import com.braintribe.gm.model.reason.essential.ParseError;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

public interface TemplateParseError extends ParseError {
	EntityType<TemplateParseError> T = EntityTypes.T(TemplateParseError.class);

	PropertyLiteral range = PropertyLiteral.of(T, "range");
	PropertyLiteral fragment = PropertyLiteral.of(T, "fragment");
	PropertyLiteral modelPath = PropertyLiteral.of(T, "modelPath");

	TextRange getRange();
	void setRange(TextRange range);

	String getFragment();
	void setFragment(String fragment);

	String getModelPath();
	void setModelPath(String modelPath);
}
