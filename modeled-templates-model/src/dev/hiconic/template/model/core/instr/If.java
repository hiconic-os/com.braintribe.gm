package dev.hiconic.template.model.core.instr;

import com.braintribe.model.generic.annotation.meta.PositionalArguments;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

import dev.hiconic.template.model.core.TemplateNode;

@PositionalArguments("condition")
public interface If extends BlockInstructionNode {
	EntityType<If> T = EntityTypes.T(If.class);
	
	PropertyLiteral else_ = PropertyLiteral.of(T, "else");
	PropertyLiteral condition = PropertyLiteral.of(T, "condition");
	
	boolean getCondition();
	void setCondition(boolean condition);
	
	TemplateNode getElse();
	void setElse(TemplateNode else_);
}
