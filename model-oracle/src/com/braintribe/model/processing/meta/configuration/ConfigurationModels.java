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

import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.processing.meta.configured.ConfigurationModelBuilder;

/**
 * A few helper methods to create new {@link ConfigurationModelBuilder} experts.
 * 
 */
public interface ConfigurationModels {

	/**
	 * A new transient {@link GmMetaModel} will be generated with name `modelName`. This can be used without a session object.
	 */
	static ConfigurationModelBuilder create(String modelName) {
		return new ConfigurationModelBuilderGmfImpl(modelName);
	}

	/**
	 * A new transient {@link GmMetaModel} is generated with name `groupId:artifactId`. This can be done without a session object.
	 */
	static ConfigurationModelBuilder create(String groupId, String artifactId) {
		return create(groupId + ":" + artifactId);
	}

	/**
	 * The existing {@link GmMetaModel} model is further extended with transient dependencies.
	 */
	static ConfigurationModelBuilder extend(GmMetaModel model) {
		return new ConfigurationModelBuilderGmfImpl(model);
	}

}
