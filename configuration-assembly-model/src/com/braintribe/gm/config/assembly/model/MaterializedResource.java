// ============================================================================
// Licensed under the Apache License, Version 2.0
// ============================================================================
package com.braintribe.gm.config.assembly.model;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/** One logical packaged resource and, only after a collision, its alternative physical path. */
public interface MaterializedResource extends GenericEntity {

	EntityType<MaterializedResource> T = EntityTypes.T(MaterializedResource.class);

	String getPath();
	void setPath(String path);

	String getMaterializedAs();
	void setMaterializedAs(String materializedAs);
}
