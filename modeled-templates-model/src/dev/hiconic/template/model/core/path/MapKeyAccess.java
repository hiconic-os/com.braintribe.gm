package dev.hiconic.template.model.core.path;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface MapKeyAccess extends PathAccess {
	EntityType<MapKeyAccess> T = EntityTypes.T(MapKeyAccess.class);
	Object getKey();
	void setKey(Object key);
	boolean getOptional();
	void setOptional(boolean optional);
}
