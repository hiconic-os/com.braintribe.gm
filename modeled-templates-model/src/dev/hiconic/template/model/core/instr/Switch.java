package dev.hiconic.template.model.core.instr;

import java.util.List;

import com.braintribe.model.generic.annotation.meta.PositionalArguments;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

import dev.hiconic.template.model.core.TemplateNode;

@PositionalArguments("value")
public interface Switch extends BlockInstructionNode {
	EntityType<Switch> T = EntityTypes.T(Switch.class);

	PropertyLiteral value = PropertyLiteral.of(T, "value");
	PropertyLiteral cases = PropertyLiteral.of(T, "cases");
	PropertyLiteral defaultBlock = PropertyLiteral.of(T, "defaultBlock");

	Object getValue();
	void setValue(Object value);

	List<SwitchCase> getCases();
	void setCases(List<SwitchCase> cases);

	TemplateNode getDefaultBlock();
	void setDefaultBlock(TemplateNode defaultBlock);
}
