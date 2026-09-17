// ============================================================================
// Licensed under the Apache License, Version 2.0
// ============================================================================
package com.braintribe.model.processing.resource.packaged.api;

import java.io.InputStream;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.model.resource.Resource;
import com.braintribe.model.resource.source.PackagedSource;

/** Resolves a file packaged in a classpath artifact, without prescribing how that artifact is physically packaged. */
public interface PackagedResourceResolver {

	/** A complete Resource for the file, backed by a resolved {@link PackagedSource}, thus persistable as an address and readable right away. */
	Maybe<Resource> resolveResource(String artifact, String path);

	/** Just the address of the file, resolved, so that a caller can keep its own Resource metadata around it. */
	Maybe<PackagedSource> resolveSource(String artifact, String path);

	/** Opens the data of a packaged file directly, for a caller that wants the bytes and no Resource at all. */
	Maybe<InputStream> openStream(String artifact, String path);
}
