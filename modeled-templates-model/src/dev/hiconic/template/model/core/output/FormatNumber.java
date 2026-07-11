package dev.hiconic.template.model.core.output;

import com.braintribe.model.generic.annotation.meta.PositionalArguments;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

@PositionalArguments("pattern")
public interface FormatNumber extends Transformer {
	EntityType<FormatNumber> T = EntityTypes.T(FormatNumber.class);

	PropertyLiteral pattern = PropertyLiteral.of(T, "pattern");
	PropertyLiteral locale = PropertyLiteral.of(T, "locale");

	String getPattern();
	void setPattern(String pattern);

	String getLocale();
	void setLocale(String locale);
}
