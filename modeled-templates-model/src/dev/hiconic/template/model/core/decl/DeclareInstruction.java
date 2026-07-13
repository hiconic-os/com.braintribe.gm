package dev.hiconic.template.model.core.decl;

import java.util.List;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

import dev.hiconic.template.model.core.instr.BlockNode;
import dev.hiconic.template.model.core.instr.SilentNode;
import dev.hiconic.template.model.core.Symbol;
import com.braintribe.model.generic.annotation.meta.PositionalArguments;

@PositionalArguments({"name", "parameters"})
public interface DeclareInstruction extends DeclarationNode, BlockNode, SilentNode {
	EntityType<DeclareInstruction> T = EntityTypes.T(DeclareInstruction.class);
	
	PropertyLiteral name = PropertyLiteral.of(T, "name");
	PropertyLiteral parameters = PropertyLiteral.of(T, "parameters");
	PropertyLiteral argumentType = PropertyLiteral.of(T, "argumentType");
	
	Symbol getName();
	void setName(Symbol name);
	
	List<VariableDefinition> getParameters();
	void setParameters(List<VariableDefinition> parameters);

	RuntimeTypeSpecification getArgumentType();
	void setArgumentType(RuntimeTypeSpecification argumentType);
}
