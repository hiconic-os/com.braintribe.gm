package dev.hiconic.template.model.core.instr;

import com.braintribe.model.generic.annotation.meta.PositionalArguments;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

@PositionalArguments({"target", "value"})
public interface Append extends CollectionMutation {
	EntityType<Append> T = EntityTypes.T(Append.class);
	PropertyLiteral value = PropertyLiteral.of(T, "value");
	Object getValue();
	void setValue(Object value);
}
