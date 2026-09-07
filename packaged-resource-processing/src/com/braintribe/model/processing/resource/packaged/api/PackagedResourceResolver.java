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

	Maybe<Resource> resolveResource(String artifact, String path);

	Maybe<PackagedSource> resolveSource(String artifact, String path);

	/**
	 * Opens the data of a packaged file.
	 * <p>
	 * A {@link PackagedSource} holds no payload, so a Resource backed by one cannot be streamed on its own. This is the way to read it.
	 */
	Maybe<InputStream> openStream(String artifact, String path);
}
