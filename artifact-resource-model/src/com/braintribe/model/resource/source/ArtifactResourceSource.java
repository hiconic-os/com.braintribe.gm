// ============================================================================
// Licensed under the Apache License, Version 2.0
// ============================================================================
package com.braintribe.model.resource.source;

import com.braintribe.model.generic.annotation.meta.Mandatory;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/** Stable address of an indexed resource within a contributing artifact. */
public interface ArtifactResourceSource extends ResourceSource {

	EntityType<ArtifactResourceSource> T = EntityTypes.T(ArtifactResourceSource.class);

	String artifact = "artifact";
	String path = "path";

	@Mandatory
	String getArtifact();
	void setArtifact(String artifact);

	@Mandatory
	String getPath();
	void setPath(String path);
}
