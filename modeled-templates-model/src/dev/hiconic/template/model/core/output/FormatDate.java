package dev.hiconic.template.model.core.output;

import com.braintribe.model.generic.annotation.meta.PositionalArguments;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

import dev.hiconic.template.model.core.vd.UnaryOperation;

@PositionalArguments({"operand", "pattern"})
public interface FormatDate extends UnaryOperation {
	EntityType<FormatDate> T = EntityTypes.T(FormatDate.class);

	PropertyLiteral pattern = PropertyLiteral.of(T, "pattern");
	PropertyLiteral locale = PropertyLiteral.of(T, "locale");
	PropertyLiteral zone = PropertyLiteral.of(T, "zone");
	PropertyLiteral zoneId = PropertyLiteral.of(T, "zoneId");

	String getPattern();
	void setPattern(String pattern);

	String getLocale();
	void setLocale(String locale);

	String getZone();
	void setZone(String zone);

	String getZoneId();
	void setZoneId(String zoneId);
}
