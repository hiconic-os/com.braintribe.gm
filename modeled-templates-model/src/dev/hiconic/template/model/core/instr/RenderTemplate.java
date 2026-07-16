package dev.hiconic.template.model.core.instr;

import com.braintribe.model.generic.annotation.meta.PositionalArguments;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

import dev.hiconic.template.model.core.TypeReference;

/** Renders a named template delegate with the supplied input value. */
@PositionalArguments({"name", "input"})
public interface RenderTemplate extends InstructionNode {
	EntityType<RenderTemplate> T = EntityTypes.T(RenderTemplate.class);

	PropertyLiteral name = PropertyLiteral.of(T, "name");
	PropertyLiteral input = PropertyLiteral.of(T, "input");
	PropertyLiteral inputType = PropertyLiteral.of(T, "inputType");

	String getName();
	void setName(String name);

	Object getInput();
	void setInput(Object input);

	TypeReference getInputType();
	void setInputType(TypeReference inputType);
}
