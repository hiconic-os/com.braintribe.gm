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
package com.braintribe.model.processing.security.query.expert;

import com.braintribe.model.processing.security.query.context.PostQueryExpertContext;
import com.braintribe.model.query.QueryResult;

/**
 * An expert that is used during the processing of the {@link QueryResult}. This expert should have one method, which is
 * able tell if something is accessible or not, according to provided {@link PostQueryExpertContext}. For technical
 * reasons, the definition of this method is left for sub-interfaces, since the parameter types differ slightly for
 * different expert types.
 * <p>
 * Example, implementation specific (SecurityAspect): Basically all the ILS experts belong to this category, as well as
 * experts for ETILS for situations, where ETILS cannot be applied in query (since only Source are adjusted for ETILS
 * conditions, but if e.g. some entity has property whose value should not be visible based on ETILS, this value is
 * returned in query result, and must be removed later).
 * 
 * @see EntityAccessExpert
 * @see PropertyRelatedAccessExpert
 */
public interface PostQueryExpert extends AccessSecurityExpert {
	// blank
}
