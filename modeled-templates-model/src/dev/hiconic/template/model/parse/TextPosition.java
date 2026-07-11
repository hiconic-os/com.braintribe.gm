package dev.hiconic.template.model.parse;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

public interface TextPosition extends GenericEntity {
	EntityType<TextPosition> T = EntityTypes.T(TextPosition.class);

	PropertyLiteral offset = PropertyLiteral.of(T, "offset");
	PropertyLiteral line = PropertyLiteral.of(T, "line");
	PropertyLiteral column = PropertyLiteral.of(T, "column");

	int getOffset();
	void setOffset(int offset);

	int getLine();
	void setLine(int line);

	int getColumn();
	void setColumn(int column);
}
