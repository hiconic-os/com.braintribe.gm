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
package com.braintribe.model.processing.session.api.managed;

import com.braintribe.model.generic.value.EntityReference;

/**
 * One of parameters of {@link ManagedGmSession#manipulate()}, which describes how manipulations related to unknown
 * entities (i.e. such that given {@link EntityReference} cannot be resolved) should be handled.
 */
public enum ManipulationLenience {

	/**
	 * Standard mode which means that manipulations related to unknown entities are not expected, in case they occur,
	 * the behavior is undefined.
	 */
	none,

	/**
	 * All manipulations related to unknown entities will be ignored.
	 */
	ignoreOnUnknownEntity,

	/**
	 * If an unknown entity is encountered, a new instance is created and manifested. In such case, the manifested
	 * entities are accessible via the {@link ManipulationReport#getLenientManifestations()}.
	 * 
	 * NOTE that a created entity has all it's properties marked as absent, except for the id property of course.
	 * 
	 * The standard use-case where this is used is the processing of induced manipulations by the session. In such case,
	 * all unknown entities are manifested and then after being applied, all the manifested entities are being
	 * "refreshed" by executing an entity query for these entities and then merging the result to the session.
	 */
	manifestOnUnknownEntity,
}
