package dev.hiconic.template.model.core.instr;

import com.braintribe.model.generic.annotation.meta.PositionalArguments;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

@PositionalArguments({"target", "key", "value"})
public interface Put extends CollectionMutation {
	EntityType<Put> T = EntityTypes.T(Put.class);
	PropertyLiteral key = PropertyLiteral.of(T, "key");
	PropertyLiteral value = PropertyLiteral.of(T, "value");
	Object getKey();
	void setKey(Object key);
	Object getValue();
	void setValue(Object value);
}
