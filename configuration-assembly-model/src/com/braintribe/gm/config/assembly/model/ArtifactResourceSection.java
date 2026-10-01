// ============================================================================
// Licensed under the Apache License, Version 2.0
// ============================================================================
package com.braintribe.gm.config.assembly.model;

import java.util.List;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/** Resource entries contributed by one artifact. */
public interface ArtifactResourceSection extends GenericEntity {

	EntityType<ArtifactResourceSection> T = EntityTypes.T(ArtifactResourceSection.class);

	List<MaterializedResource> getResources();
	void setResources(List<MaterializedResource> resources);
}
