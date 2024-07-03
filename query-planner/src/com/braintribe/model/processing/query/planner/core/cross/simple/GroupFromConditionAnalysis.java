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

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.braintribe.model.processing.query.planner.core.cross.FromGroup;
import com.braintribe.model.query.From;
import com.braintribe.model.query.conditions.Condition;

/**
 * 
 */
class GroupFromConditionAnalysis {

	final Map<ConditionApplicationType, Set<StepDescription>> stepByApplicationType;
	final Map<Condition, Set<From>> fromsForCondition;
	final Map<Condition, Set<FromGroup>> groupsForCondition;
	final Set<From> joinableFroms; // TODO this seems to be not used, it's never set to anything.

	public GroupFromConditionAnalysis(Map<ConditionApplicationType, Set<StepDescription>> stepByApplicationType,
			Map<Condition, Set<From>> fromsForCondition, Map<Condition, Set<FromGroup>> groupsForCondition, Set<From> joinableFroms) {

		this.stepByApplicationType = stepByApplicationType;
		this.fromsForCondition = fromsForCondition;
		this.groupsForCondition = groupsForCondition;
		this.joinableFroms = joinableFroms;
	}

	public Set<From> fromsForCondition(Condition condition) {
		return notNull(fromsForCondition.get(condition));
	}

	public Set<FromGroup> groupsForCondition(Condition condition) {
		return notNull(groupsForCondition.get(condition));
	}

	public boolean isJoinable(From from) {
		return joinableFroms.contains(from);
	}

	private <T> Set<T> notNull(Set<T> set) {
		return set != null ? set : Collections.<T> emptySet();
	}

}
