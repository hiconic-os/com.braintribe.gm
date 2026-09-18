// ============================================================================
// Licensed under the Apache License, Version 2.0
// ============================================================================
package com.braintribe.model.resource.source;

import com.braintribe.model.generic.annotation.Transient;
import com.braintribe.model.generic.annotation.meta.Mandatory;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.session.InputStreamProvider;

/**
 * Stable address of a file packaged in a classpath artifact, given by the artifact and relative path within that artifact.
 * <p>
 * Not that only files indexed via META-INF/classpath-index.txt are meant to be addressable.
 */
public interface PackagedSource extends ResourceSource, StreamableSource {

	EntityType<PackagedSource> T = EntityTypes.T(PackagedSource.class);

	String artifact = "artifact";
	String path = "path";
	String inputStreamProvider = "inputStreamProvider";

	/** Name of the artifact without a version, i.e. ${groupId}:${artifactId} */
	@Mandatory
	String getArtifact();
	void setArtifact(String artifact);

	/**
	 * {@code /}-separated path, compatible as input for {@link ClassLoader#resources(String)}.
	 * <p>
	 * Note this method can return multiple resources, from different artifacts, but with an artifact given it is unambiguous.
	 */
	@Mandatory
	String getPath();
	void setPath(String path);

	/**
	 * {@link InputStreamProvider} for the underlying resource, addressed by given artifact and path.
	 * <p>
	 * This property is typically set by the creator of this instance (e.g. configuration parser), in order to make the instance streamable.
	 */
	@Transient
	InputStreamProvider getInputStreamProvider();
	void setInputStreamProvider(InputStreamProvider inputStreamProvider);

	@Override
	default InputStreamProvider inputStreamProvider() {
		return getInputStreamProvider();
	}

	/**
	 * Says that this source holds a transient value worth carrying onto a clone, which is the only thing this flag is used for. It does not mean the
	 * payload lives in memory: what is stored is still the address.
	 */
	@Override
	default boolean hasTransientData() {
		return getInputStreamProvider() != null;
	}

}
