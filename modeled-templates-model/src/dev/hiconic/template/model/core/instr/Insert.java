package dev.hiconic.template.model.core.instr;

import com.braintribe.model.generic.annotation.meta.PositionalArguments;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

@PositionalArguments({"target", "index", "value"})
public interface Insert extends CollectionMutation {
	EntityType<Insert> T = EntityTypes.T(Insert.class);
	PropertyLiteral index = PropertyLiteral.of(T, "index");
	PropertyLiteral value = PropertyLiteral.of(T, "value");
	int getIndex();
	void setIndex(int index);
	Object getValue();
	void setValue(Object value);
}
