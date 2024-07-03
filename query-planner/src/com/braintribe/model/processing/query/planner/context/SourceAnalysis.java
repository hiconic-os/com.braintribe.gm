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
package com.braintribe.model.processing.query.planner.context;

import static com.braintribe.utils.lcd.CollectionTools2.newList;
import static com.braintribe.utils.lcd.CollectionTools2.newMap;
import static com.braintribe.utils.lcd.CollectionTools2.newSet;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.braintribe.model.query.From;
import com.braintribe.model.query.Join;
import com.braintribe.model.query.Source;

/**
 * 
 */
class SourceAnalysis {

	/**
	 * All sources for given query.
	 */
	List<Source> relevantSources = newList();

	/**
	 * All the joins we have to do - those needed for the 'select' and 'order by' clauses.
	 */
	Set<Join> mandatoryJoins = newSet();

	/**
	 * Sub-sequence of {@link #relevantSources} consisting strictly of all the right joins.
	 */
	List<Join> rightJoins = newList();

	/**
	 * Maps each source to the root {@link From}. For from this maps to the From itself, for joins it maps to the From
	 * that is at the beginning of the Join-chain.
	 */
	Map<Source, From> sourceRoot = newMap();

}
