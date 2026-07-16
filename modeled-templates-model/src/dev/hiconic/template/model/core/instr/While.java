package dev.hiconic.template.model.core.instr;

import com.braintribe.model.generic.annotation.meta.PositionalArguments;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

@PositionalArguments("condition")
public interface While extends BlockInstructionNode, BreakableNode, ContinuableNode {
	EntityType<While> T = EntityTypes.T(While.class);

	PropertyLiteral condition = PropertyLiteral.of(T, "condition");

	Object getCondition();
	void setCondition(Object condition);
}
