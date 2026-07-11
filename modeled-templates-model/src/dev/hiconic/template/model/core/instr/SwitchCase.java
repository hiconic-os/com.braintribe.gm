package dev.hiconic.template.model.core.instr;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

import dev.hiconic.template.model.core.TemplateNode;

public interface SwitchCase extends GenericEntity {
	EntityType<SwitchCase> T = EntityTypes.T(SwitchCase.class);

	PropertyLiteral value = PropertyLiteral.of(T, "value");
	PropertyLiteral block = PropertyLiteral.of(T, "block");

	Object getValue();
	void setValue(Object value);

	TemplateNode getBlock();
	void setBlock(TemplateNode block);
}
