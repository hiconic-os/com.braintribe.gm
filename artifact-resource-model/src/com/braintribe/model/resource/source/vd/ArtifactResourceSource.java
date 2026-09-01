// ============================================================================
// Licensed under the Apache License, Version 2.0
// ============================================================================
package com.braintribe.model.resource.source.vd;

import com.braintribe.model.generic.annotation.meta.PositionalArguments;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.value.ValueDescriptor;
import com.braintribe.model.resource.source.ResourceSource;

/** Resolves an artifact-relative indexed payload as a {@link ResourceSource}. */
@PositionalArguments({ "path", "artifact" })
public interface ArtifactResourceSource extends ValueDescriptor {

	EntityType<ArtifactResourceSource> T = EntityTypes.T(ArtifactResourceSource.class);

	String path = "path";
	String artifact = "artifact";

	String getPath();
	void setPath(String path);

	/** Optional in an expression and completed from its owning source context. */
	String getArtifact();
	void setArtifact(String artifact);

	@Override
	default GenericModelType valueType() {
		return ResourceSource.T;
	}
}
