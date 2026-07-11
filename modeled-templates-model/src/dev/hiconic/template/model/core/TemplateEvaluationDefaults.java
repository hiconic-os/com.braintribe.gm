package dev.hiconic.template.model.core;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

public interface TemplateEvaluationDefaults extends GenericEntity {
	EntityType<TemplateEvaluationDefaults> T = EntityTypes.T(TemplateEvaluationDefaults.class);

	PropertyLiteral locale = PropertyLiteral.of(T, "locale");
	PropertyLiteral zoneId = PropertyLiteral.of(T, "zoneId");
	PropertyLiteral datePattern = PropertyLiteral.of(T, "datePattern");
	PropertyLiteral numberPattern = PropertyLiteral.of(T, "numberPattern");

	String getLocale();
	void setLocale(String locale);

	String getZoneId();
	void setZoneId(String zoneId);

	String getDatePattern();
	void setDatePattern(String datePattern);

	String getNumberPattern();
	void setNumberPattern(String numberPattern);
}
