package dev.hiconic.template.model.core.instr;

import com.braintribe.model.generic.annotation.meta.PositionalArguments;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

@PositionalArguments({"target", "index"})
public interface RemoveAt extends CollectionMutation {
	EntityType<RemoveAt> T = EntityTypes.T(RemoveAt.class);
	PropertyLiteral index = PropertyLiteral.of(T, "index");
	Object getIndex();
	void setIndex(Object index);
}
