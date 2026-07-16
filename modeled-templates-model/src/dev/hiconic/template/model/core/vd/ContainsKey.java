package dev.hiconic.template.model.core.vd;

import com.braintribe.model.generic.annotation.meta.PositionalArguments;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;
import com.braintribe.model.generic.value.type.BooleanDescriptor;

/** Tests whether a map contains a key. */
@PositionalArguments({"map", "key"})
public interface ContainsKey extends BooleanDescriptor {
	EntityType<ContainsKey> T = EntityTypes.T(ContainsKey.class);

	PropertyLiteral map = PropertyLiteral.of(T, "map");
	PropertyLiteral key = PropertyLiteral.of(T, "key");

	Object getMap();
	void setMap(Object map);

	Object getKey();
	void setKey(Object key);
}
