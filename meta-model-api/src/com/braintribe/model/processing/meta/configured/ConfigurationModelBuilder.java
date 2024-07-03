// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ============================================================================
package com.braintribe.model.processing.meta.configured;

import com.braintribe.common.artifact.ArtifactReflection;
import com.braintribe.model.generic.reflection.Model;
import com.braintribe.model.meta.GmMetaModel;

/**
 * The interface to generate new (configuration) {@link GmMetaModel model} given a name, version and dependency information.
 * 
 * Some of the functionality depends on the implementation, e.g. the globalId is only useful with aManagedGmSession.
 * 
 * Calls to the interface can be chained, and the final {@link #get()} is optional to return the assembled model.
 * 
 * @author Dirk Scheffler
 */
public interface ConfigurationModelBuilder {

	/**
	 * @param modelName
	 *            A fully qualified model name, i.e. "${groupId}:${artifactId}"``
	 */
	ConfigurationModelBuilder addDependencyByName(String modelName);

	/**
	 * @param modelArtifactReflection
	 *            {@link ArtifactReflection} for a model artifact.
	 */
	ConfigurationModelBuilder addDependency(ArtifactReflection modelArtifactReflection);
	
	ConfigurationModelBuilder addDependency(GmMetaModel gmModel);
	
	ConfigurationModelBuilder addDependency(Model model);

	/** Only available if the implementation is backed by a ManagedGmSession */
	ConfigurationModelBuilder addDependencyByGlobalId(String globalId); // only on session

	/** @return The assembled {@link GmMetaModel} with all the desired dependencies. */
	GmMetaModel get();
}
