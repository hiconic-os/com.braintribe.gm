package dev.hiconic.template.model.core.instr;

import com.braintribe.model.generic.annotation.meta.PositionalArguments;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

@PositionalArguments("condition")
public interface When extends SwitchCase {
	EntityType<When> T = EntityTypes.T(When.class);

	PropertyLiteral condition = PropertyLiteral.of(T, "condition");

	Object getCondition();
	void setCondition(Object condition);
}
