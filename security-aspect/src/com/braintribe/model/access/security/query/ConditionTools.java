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
package com.braintribe.model.access.security.query;

import java.util.Arrays;
import java.util.List;

import com.braintribe.model.query.conditions.Condition;
import com.braintribe.model.query.conditions.Conjunction;
import com.braintribe.model.query.conditions.Disjunction;
import com.braintribe.model.query.conditions.Negation;

/**
 * 
 */
public class ConditionTools {

	public static Negation not(Condition condition) {
		Negation negation = Negation.T.create();
		negation.setOperand(condition);
		return negation;
	}

	public static Conjunction and(Condition... conditions) {
		return and(Arrays.asList(conditions));
	}

	public static Conjunction and(List<Condition> conditions) {
		Conjunction and = Conjunction.T.create();
		and.setOperands(conditions);
		return and;
	}

	public static Disjunction or(Condition... conditions) {
		return or(Arrays.asList(conditions));
	}

	public static Disjunction or(List<Condition> conditions) {
		Disjunction result = Disjunction.T.create();
		result.setOperands(conditions);
		return result;
	}

}
