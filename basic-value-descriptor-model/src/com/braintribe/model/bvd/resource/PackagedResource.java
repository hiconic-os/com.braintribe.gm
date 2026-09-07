// ============================================================================
// Licensed under the Apache License, Version 2.0
// ============================================================================
package com.braintribe.model.bvd.resource;

import com.braintribe.model.generic.annotation.meta.PositionalArguments;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.value.ValueDescriptor;
import com.braintribe.model.resource.Resource;

/**
 * Resolves a file packaged in a classpath artifact as a complete {@link Resource}, written as
 * <code>${packagedResource('./logo.svg')}</code>.
 * <p>
 * Everything about the Resource, including its name and mime type, is derived from the file. Use {@link PackagedSource} instead when the Resource
 * carries modeled metadata that must survive.
 */
@PositionalArguments({ "path", "artifact" })
public interface PackagedResource extends ValueDescriptor {

	EntityType<PackagedResource> T = EntityTypes.T(PackagedResource.class);

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
		return Resource.T;
	}
}
