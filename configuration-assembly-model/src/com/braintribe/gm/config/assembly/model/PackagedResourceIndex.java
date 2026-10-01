// ============================================================================
// Licensed under the Apache License, Version 2.0
// ============================================================================
package com.braintribe.gm.config.assembly.model;

import java.util.Map;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.Initializer;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/** Maps artifact-owned logical resource paths to their materialized paths in an assembled application. */
public interface PackagedResourceIndex extends GenericEntity {

	EntityType<PackagedResourceIndex> T = EntityTypes.T(PackagedResourceIndex.class);

	@Initializer("1")
	int getFormatVersion();
	void setFormatVersion(int formatVersion);

	Map<String, ArtifactResourceSection> getArtifacts();
	void setArtifacts(Map<String, ArtifactResourceSection> artifacts);
}
