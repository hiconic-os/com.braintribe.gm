package dev.hiconic.template.model.core.instr;

import com.braintribe.model.generic.annotation.meta.PositionalArguments;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

import dev.hiconic.template.model.core.TemplateNode;
import dev.hiconic.template.model.core.decl.VariableDefinition;

/** Iterates a map with independently optional, statically typed key and value bindings. */
@PositionalArguments({"iterable"})
public interface ForEachEntry extends BlockInstructionNode, BreakableNode, ContinuableNode {
	EntityType<ForEachEntry> T = EntityTypes.T(ForEachEntry.class);
	PropertyLiteral iterable = PropertyLiteral.of(T, "iterable");
	PropertyLiteral key = PropertyLiteral.of(T, "key");
	PropertyLiteral value = PropertyLiteral.of(T, "value");
	PropertyLiteral empty = PropertyLiteral.of(T, "empty");

	Object getIterable();
	void setIterable(Object iterable);
	VariableDefinition getKey();
	void setKey(VariableDefinition key);
	VariableDefinition getValue();
	void setValue(VariableDefinition value);
	TemplateNode getEmpty();
	void setEmpty(TemplateNode empty);
}
