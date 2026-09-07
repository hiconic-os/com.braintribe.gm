// ============================================================================
// Licensed under the Apache License, Version 2.0
// ============================================================================
package com.braintribe.model.bvd.resource;

import com.braintribe.model.generic.annotation.meta.PositionalArguments;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.value.ValueDescriptor;
import com.braintribe.model.resource.source.ResourceSource;

/**
 * Resolves a file packaged in a classpath artifact as a {@link ResourceSource}, written as
 * <code>${packagedSource('./logo.svg')}</code>.
 * <p>
 * Only the source is resolved. The surrounding Resource, with its name, mime type and tags, stays modeled explicitly.
 */
@PositionalArguments({ "path", "artifact" })
public interface PackagedSource extends ValueDescriptor {

	EntityType<PackagedSource> T = EntityTypes.T(PackagedSource.class);

	String path = "path";
	String artifact = "artifact";

	/** {@code ./} and {@code ../} are relative to the document that holds this expression, every other path is artifact root relative. */
	String getPath();
	void setPath(String path);

	/** Optional in an expression, and completed from the artifact that owns the document. */
	String getArtifact();
	void setArtifact(String artifact);

	@Override
	default GenericModelType valueType() {
		return ResourceSource.T;
	}
}
