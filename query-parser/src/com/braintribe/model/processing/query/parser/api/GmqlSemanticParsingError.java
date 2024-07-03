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
package com.braintribe.model.processing.query.parser.api;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * A representation for semantic errors encountered during parsing. One should
 * only encounter this type of error, only if syntactically everything was
 * sound. Examples for such error:
 * <ul>
 * <li>"Join provided with no defined sourceAlias, propertyName [xyz]"</li>
 * <li>"Source expected and not registered, provided alias: [xyz]"</li>
 * </ul>
 * 
 * In addition to the semantic problems, this error is used seldom to represent
 * errors encountered in some evaluations that use external components, eg. Date
 * evaluation.
 */

public interface GmqlSemanticParsingError extends GmqlParsingError {

	EntityType<GmqlSemanticParsingError> T = EntityTypes.T(GmqlSemanticParsingError.class);

}
