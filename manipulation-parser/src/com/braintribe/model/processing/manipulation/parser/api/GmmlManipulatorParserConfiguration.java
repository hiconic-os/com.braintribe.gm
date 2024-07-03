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
import com.braintribe.model.processing.manipulation.parser.impl.listener.error.StrictErrorHandler;

public interface GmmlManipulatorParserConfiguration extends GmmlParserConfiguration {

	/**
	 * Registry for {@link ProblematicEntitiesRegistry#isProblematic(String) problematic entities}.
	 * <p>
	 * If the manipulator-parser encounters a problematic entity (i.e. a lookup of given globalId happens), then it raises the
	 * {@link GmmlManipulatorErrorHandler#problematicEntityReferenced(String)}.
	 * <p>
	 * The basic use-case for using this is lenient parsing of successive files (e.g. CSA stages), where errors in one file are ignored, but the
	 * corresponding entities are collected so we can report a possible follow-up problems in the files after.
	 * <p>
	 * After the parser is done, and there were new problematic entities discovered (and the errors were handled leniently), the parser calls
	 * {@link ProblematicEntitiesRegistry#recognizeProblematicEntities(java.util.Set)} with
	 */
	ProblematicEntitiesRegistry problematicEntitiesRegistry();

	/** If null is returned, the parser uses {@link StrictErrorHandler} per default. */
	GmmlManipulatorErrorHandler errorHandler();

	Set<GenericEntity> previouslyCreatedEntities();

	Set<String> homeopathicVariables();

}
