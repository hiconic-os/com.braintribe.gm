// ============================================================================
// Licensed under the Apache License, Version 2.0
// ============================================================================
package com.braintribe.model.resource.source.vd;

import com.braintribe.model.generic.annotation.meta.PositionalArguments;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.value.ValueDescriptor;
import com.braintribe.model.resource.Resource;

/** Resolves an artifact-relative indexed payload as a complete {@link Resource}. */
@PositionalArguments({ "path", "artifact" })
public interface ArtifactResource extends ValueDescriptor {

	EntityType<ArtifactResource> T = EntityTypes.T(ArtifactResource.class);

	String path = "path";
	String artifact = "artifact";

	/** {@code ./} and {@code ../} are source-document-relative; every other path is artifact-root-relative. */
	String getPath();
	void setPath(String path);

	/** Optional in an expression and completed from its owning source context. */
	String getArtifact();
	void setArtifact(String artifact);

	@Override
	default GenericModelType valueType() {
		return Resource.T;
	}
}
