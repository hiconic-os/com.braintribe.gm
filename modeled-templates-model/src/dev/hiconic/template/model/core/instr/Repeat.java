package dev.hiconic.template.model.core.instr;

import com.braintribe.model.generic.annotation.meta.PositionalArguments;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

import dev.hiconic.template.model.core.decl.VariableDefinition;

@PositionalArguments({"count", "indexVariable"})
public interface Repeat extends BlockInstructionNode, BreakableNode, ContinuableNode {
	EntityType<Repeat> T = EntityTypes.T(Repeat.class);

	PropertyLiteral count = PropertyLiteral.of(T, "count");
	PropertyLiteral indexVariable = PropertyLiteral.of(T, "indexVariable");

	Object getCount();
	void setCount(Object count);

	VariableDefinition getIndexVariable();
	void setIndexVariable(VariableDefinition indexVariable);
}
