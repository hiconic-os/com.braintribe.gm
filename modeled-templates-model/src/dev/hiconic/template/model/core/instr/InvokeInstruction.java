package dev.hiconic.template.model.core.instr;

import com.braintribe.model.generic.annotation.meta.PositionalArguments;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

import dev.hiconic.template.model.core.decl.DeclareInstruction;
import dev.hiconic.template.model.core.decl.RuntimeArguments;
import dev.hiconic.template.model.core.TemplateNode;

@PositionalArguments("name")
public interface InvokeInstruction extends InstructionNode {
	EntityType<InvokeInstruction> T = EntityTypes.T(InvokeInstruction.class);

	PropertyLiteral name = PropertyLiteral.of(T, "name");
	PropertyLiteral arguments = PropertyLiteral.of(T, "arguments");
	PropertyLiteral declaration = PropertyLiteral.of(T, "declaration");
	PropertyLiteral body = PropertyLiteral.of(T, "body");

	String getName();
	void setName(String name);

	DeclareInstruction getDeclaration();
	void setDeclaration(DeclareInstruction declaration);

	RuntimeArguments getArguments();
	void setArguments(RuntimeArguments arguments);

	TemplateNode getBody();
	void setBody(TemplateNode body);
}
