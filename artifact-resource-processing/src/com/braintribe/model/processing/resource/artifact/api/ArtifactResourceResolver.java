// ============================================================================
// Licensed under the Apache License, Version 2.0
// ============================================================================
package com.braintribe.model.processing.resource.artifact.api;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.model.resource.Resource;
import com.braintribe.model.resource.source.ArtifactResourceSource;

/** Resolves artifact-relative indexed resources without prescribing their physical packaging. */
public interface ArtifactResourceResolver {

	Maybe<Resource> resolveResource(String artifact, String path);

	Maybe<ArtifactResourceSource> resolveSource(String artifact, String path);
}
