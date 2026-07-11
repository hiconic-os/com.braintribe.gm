package dev.hiconic.template.model.parse;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

public interface TextRange extends GenericEntity {
	EntityType<TextRange> T = EntityTypes.T(TextRange.class);

	PropertyLiteral start = PropertyLiteral.of(T, "start");
	PropertyLiteral end = PropertyLiteral.of(T, "end");

	TextPosition getStart();
	void setStart(TextPosition start);

	TextPosition getEnd();
	void setEnd(TextPosition end);
}
