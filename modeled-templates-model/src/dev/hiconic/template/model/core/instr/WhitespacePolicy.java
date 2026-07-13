package dev.hiconic.template.model.core.instr;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

public interface WhitespacePolicy extends GenericEntity {
	EntityType<WhitespacePolicy> T = EntityTypes.T(WhitespacePolicy.class);

	PropertyLiteral before = PropertyLiteral.of(T, "before");
	PropertyLiteral after = PropertyLiteral.of(T, "after");

	WhitespaceAction getBefore();
	void setBefore(WhitespaceAction before);

	WhitespaceAction getAfter();
	void setAfter(WhitespaceAction after);
}
