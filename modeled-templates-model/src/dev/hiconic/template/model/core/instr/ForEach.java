package dev.hiconic.template.model.core.instr;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

public interface ForEach extends BlockInstructionNode {
	EntityType<ForEach> T = EntityTypes.T(ForEach.class);

	PropertyLiteral iterable = PropertyLiteral.of(T, "iterable");
	PropertyLiteral variable = PropertyLiteral.of(T, "variable");
	PropertyLiteral indexVariable = PropertyLiteral.of(T, "indexVariable");
	PropertyLiteral empty = PropertyLiteral.of(T, "empty");

	Object getIterable();
	void setIterable(Object iterable);

	String getVariable();
	void setVariable(String variable);

	String getIndexVariable();
	void setIndexVariable(String indexVariable);

	dev.hiconic.template.model.core.TemplateNode getEmpty();
	void setEmpty(dev.hiconic.template.model.core.TemplateNode empty);
}
