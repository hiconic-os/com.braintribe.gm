package dev.hiconic.template.model.core.path;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface ListIndexAccess extends PathAccess {
	EntityType<ListIndexAccess> T = EntityTypes.T(ListIndexAccess.class);
	Object getIndex();
	void setIndex(Object index);
	boolean getOptional();
	void setOptional(boolean optional);
}
