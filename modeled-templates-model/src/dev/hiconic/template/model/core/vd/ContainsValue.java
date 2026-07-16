package dev.hiconic.template.model.core.vd;

import com.braintribe.model.generic.annotation.meta.PositionalArguments;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;
import com.braintribe.model.generic.value.type.BooleanDescriptor;

/** Tests whether a map contains a value. */
@PositionalArguments({"map", "value"})
public interface ContainsValue extends BooleanDescriptor {
	EntityType<ContainsValue> T = EntityTypes.T(ContainsValue.class);

	PropertyLiteral map = PropertyLiteral.of(T, "map");
	PropertyLiteral value = PropertyLiteral.of(T, "value");

	Object getMap();
	void setMap(Object map);

	Object getValue();
	void setValue(Object value);
}
