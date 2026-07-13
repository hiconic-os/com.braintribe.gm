package dev.hiconic.template.model.core.path;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface PropertyAccess extends PathAccess {
	EntityType<PropertyAccess> T = EntityTypes.T(PropertyAccess.class);
	PropertyReference getProperty();
	void setProperty(PropertyReference property);
	boolean getOptional();
	void setOptional(boolean optional);
}
