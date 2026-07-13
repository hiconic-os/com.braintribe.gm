package dev.hiconic.template.model.core;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.meta.PositionalArguments;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

/** A scalar entity whose syntax consumes the un-tokenized remainder of an argument list. */
@PositionalArguments("value")
public interface SourceText extends GenericEntity {
	EntityType<SourceText> T = EntityTypes.T(SourceText.class);
	PropertyLiteral value = PropertyLiteral.of(T, "value");
	String getValue();
	void setValue(String value);
}
