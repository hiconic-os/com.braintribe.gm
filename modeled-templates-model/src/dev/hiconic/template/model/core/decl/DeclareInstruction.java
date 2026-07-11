package dev.hiconic.template.model.core.decl;

import java.util.List;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

import dev.hiconic.template.model.core.instr.BlockInstructionNode;

public interface DeclareInstruction extends DeclarationNode, BlockInstructionNode {
	EntityType<DeclareInstruction> T = EntityTypes.T(DeclareInstruction.class);
	
	PropertyLiteral name = PropertyLiteral.of(T, "name");
	PropertyLiteral parameters = PropertyLiteral.of(T, "parameters");
	PropertyLiteral argumentType = PropertyLiteral.of(T, "argumentType");
	
	String getName();
	void setName(String name);
	
	List<Parameter> getParameters();
	void setParameters(List<Parameter> parameters);

	RuntimeTypeSpecification getArgumentType();
	void setArgumentType(RuntimeTypeSpecification argumentType);
}
