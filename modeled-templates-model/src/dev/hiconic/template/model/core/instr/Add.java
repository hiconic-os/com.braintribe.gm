package dev.hiconic.template.model.core.instr;

import com.braintribe.model.generic.annotation.meta.PositionalArguments;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

@PositionalArguments({"target", "value"})
public interface Add extends CollectionMutation {
	EntityType<Add> T = EntityTypes.T(Add.class);
	PropertyLiteral value = PropertyLiteral.of(T, "value");
	Object getValue();
	void setValue(Object value);
}
