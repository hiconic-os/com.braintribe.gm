package dev.hiconic.template.test.model;

import com.braintribe.model.generic.annotation.meta.PositionalArguments;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

import dev.hiconic.template.model.core.TemplateNode;
import dev.hiconic.template.model.core.instr.BlockInstructionNode;

@PositionalArguments("condition")
public interface TestBlockInstruction extends BlockInstructionNode {
	EntityType<TestBlockInstruction> T = EntityTypes.T(TestBlockInstruction.class);

	PropertyLiteral condition = PropertyLiteral.of(T, "condition");
	PropertyLiteral fallback = PropertyLiteral.of(T, "fallback");

	boolean getCondition();
	void setCondition(boolean condition);

	TemplateNode getFallback();
	void setFallback(TemplateNode fallback);
}
