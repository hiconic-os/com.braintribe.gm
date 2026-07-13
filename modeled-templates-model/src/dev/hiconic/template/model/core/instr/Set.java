package dev.hiconic.template.model.core.instr;

import com.braintribe.model.generic.annotation.meta.PositionalArguments;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

@PositionalArguments({"var", "value"})
public interface Set extends StatementInstructionNode {
	EntityType<Set> T = EntityTypes.T(Set.class);

	PropertyLiteral var = PropertyLiteral.of(T, "var");
	PropertyLiteral value = PropertyLiteral.of(T, "value");
	
	AssignmentTarget getVar();
	void setVar(AssignmentTarget var);
	
	Object getValue();
	void setValue(Object value);
}
