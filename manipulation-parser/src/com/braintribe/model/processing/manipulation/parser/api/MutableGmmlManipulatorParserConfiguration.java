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
package com.braintribe.model.processing.manipulation.parser.api;

import java.util.Set;

import com.braintribe.model.generic.GenericEntity;

public interface MutableGmmlManipulatorParserConfiguration extends GmmlManipulatorParserConfiguration, MutableGmmlParserConfiguration {

	/** @see #errorHandler() */
	void setErrorHandler(GmmlManipulatorErrorHandler errorHandler);

	/** @see #problematicEntitiesRegistry() */
	void setProblematicEntitiesRegistry(ProblematicEntitiesRegistry registry);

	/** The set might be modified internally, so if a copy is needed, do it yourself. */
	void setPreviouslyCreatedEntities(Set<GenericEntity> createdEntities);

	/**
	 * Variables for entities which are created and also deleted in the parsed GMML text. This information has to probably be obtained by doing a
	 * pre-processing of the text, and could improve performance.
	 */
	void setHomeopathicVariables(Set<String> homeopathicVariables);

}
