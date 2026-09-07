// ============================================================================
// Licensed under the Apache License, Version 2.0
// ============================================================================
package com.braintribe.model.resource.source;

import com.braintribe.model.generic.annotation.meta.Mandatory;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * Stable address of a file packaged in a classpath artifact, given by the artifact and the artifact relative path.
 */
public interface PackagedSource extends ResourceSource {

	EntityType<PackagedSource> T = EntityTypes.T(PackagedSource.class);

	String artifact = "artifact";
	String path = "path";

	@Mandatory
	String getArtifact();
	void setArtifact(String artifact);

	@Mandatory
	String getPath();
	void setPath(String path);

}
