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
package com.braintribe.model.processing.meta.configuration;

import com.braintribe.common.artifact.ArtifactReflection;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelTypeReflection;
import com.braintribe.model.generic.reflection.Model;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.processing.meta.configured.ConfigurationModelBuilder;

/**
 * A {@link ConfigurationModelBuilder} which looks up dependencies via {@link GenericModelTypeReflection#findModel(String) type reflection}.
 * <p>
 * When creating a new {@link GmMetaModel}, i.e. not editing existing one, creation is done via {@link EntityType#create()}, i.e. not associated with
 * any session.
 * 
 * @author Dirk Scheffler
 */
public class ConfigurationModelBuilderGmfImpl implements ConfigurationModelBuilder {

	private final GmMetaModel model;

	/**
	 * @param modelName
	 *            The name of the {@link GmMetaModel} to be generated.
	 */
	public ConfigurationModelBuilderGmfImpl(String modelName) {
		this.model = GmMetaModel.T.create();
		this.model.setName(modelName);
	}

	/**
	 * @param model
	 *            An existing {@link GmMetaModel} to be extended.
	 */
	public ConfigurationModelBuilderGmfImpl(GmMetaModel model) {
		this.model = model;
	}

	@Override
	public ConfigurationModelBuilder addDependency(ArtifactReflection artifactReflection) {
		if (!artifactReflection.archetypes().contains("model"))
			throw new IllegalArgumentException("Artifact " + artifactReflection + " is not a model");

		return addDependencyByName(artifactReflection.name());
	}

	@Override
	public ConfigurationModelBuilder addDependencyByName(String modelName) {
		return addDependency(GMF.getTypeReflection().getModel(modelName));
	}

	@Override
	public ConfigurationModelBuilder addDependency(Model dependency) {
		return addDependency((GmMetaModel) dependency.getMetaModel());
	}

	@Override
	public ConfigurationModelBuilder addDependency(GmMetaModel dependency) {
		model.getDependencies().add(dependency);
		return this;
	}

	@Override
	public GmMetaModel get() {
		return model;
	}

	/**
	 * <b>WARNING: Using {@link GMF} this is not possible and will throw.</b>
	 */
	@Override
	public ConfigurationModelBuilder addDependencyByGlobalId(String globalId) {
		throw new UnsupportedOperationException("Model lookup by globalId is not supported by this implementation.");
	}
}
