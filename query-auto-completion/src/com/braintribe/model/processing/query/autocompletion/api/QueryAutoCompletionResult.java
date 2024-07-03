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
package com.braintribe.model.processing.query.autocompletion.api;

import java.util.Set;

import com.braintribe.model.generic.StandardIdentifiable;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.meta.GmType;

/**
 * Result returned to the QueryModelEditor panel (with the last of actual possible hints, displayed to the users).
 *
 */
public interface QueryAutoCompletionResult extends StandardIdentifiable {
	
	final EntityType<QueryAutoCompletionResult> T = EntityTypes.T(QueryAutoCompletionResult.class);

	Set<String> getPossibleHints();

	void setPossibleHints(Set<String> value);
	
	String getTypeSignature();
	
	void setTypeSignature(String typeSignature);
	
	String getFilterString();
	
	void setFilterString(String filterString);
	
	GmType getAliasType();
	
	void setAliasType(GmType aliasType);
	
	Set<String> getPossibleHintsFilteredOut();
	
	void setPossibleHintsFilteredOut(Set<String> possibleHintsFilteredOut);
}
