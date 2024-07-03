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
package com.braintribe.model.processing.query.planner.core.cross.simple;

import java.util.Set;

import com.braintribe.model.processing.query.planner.context.QueryPlannerContext;
import com.braintribe.model.processing.query.planner.core.cross.FromGroup;
import com.braintribe.model.query.From;
import com.braintribe.model.query.conditions.Disjunction;

@Deprecated // unused
class DisjunctionResolver {

	private final QueryPlannerContext context;

	/**
	 * @param resolver
	 *            is not used right now, but might be if we want to implement some optimization in the future
	 */
	DisjunctionResolver(CrossJoinOrderResolver resolver, QueryPlannerContext context) {
		this.context = context;
	}

	FromGroup resolveFor(Set<FromGroup> groups, Set<From> froms, Disjunction disjunction) {
		return CrossJoinOrderResolver.filteredCartesianProduct(groups, froms, disjunction, context);
	}

}
