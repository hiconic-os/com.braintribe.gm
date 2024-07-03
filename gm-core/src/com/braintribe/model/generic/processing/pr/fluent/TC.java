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
package com.braintribe.model.generic.processing.pr.fluent;

import static com.braintribe.utils.lcd.CollectionTools2.nullSafe;

import java.util.function.Consumer;

import com.braintribe.model.generic.pr.criteria.JunctionCriterion;
import com.braintribe.model.generic.pr.criteria.NegationCriterion;
import com.braintribe.model.generic.pr.criteria.PlaceholderCriterion;
import com.braintribe.model.generic.pr.criteria.TraversingCriterion;
import com.braintribe.model.generic.pr.criteria.TypeConditionCriterion;
import com.braintribe.model.generic.typecondition.TypeCondition;

public class TC {

	public static CriterionBuilder<TC> create() {
		TC tc = new TC();
		return tc.getCriterionBuilder(tc, criterion -> tc.result = criterion);
	}

	protected TraversingCriterion result;

	protected TC() {
	}

	public TraversingCriterion done() {
		return result;
	}

	protected CriterionBuilder<TC> getCriterionBuilder(TC tc, Consumer<TraversingCriterion> receiver) {
		return new CriterionBuilder<TC>(tc, receiver);
	}

	// Temporary, please don't use yet
	
	// ############################################################
	// ## . . . . . . . . Simple builder methods . . . . . . . . ##
	// ############################################################

	public static PlaceholderCriterion placeholder(String name) {
		PlaceholderCriterion result = PlaceholderCriterion.T.create();
		result.setName(name);
		return result;
	}

	public static NegationCriterion negation(TraversingCriterion tc) {
		NegationCriterion result = NegationCriterion.T.create();
		result.setCriterion(tc);
		return result;
	}

	public static TypeConditionCriterion typeCondition(TypeCondition typeCondition) {
		TypeConditionCriterion result = TypeConditionCriterion.T.create();
		result.setTypeCondition(typeCondition);
		return result;
	}

	public static boolean containsPlaceholder(TraversingCriterion tc) {
		if (tc == null)
			return false;

		switch (tc.criterionType()) {
			case CONJUNCTION:
			case DISJUNCTION:
				return containsPlaceholder((JunctionCriterion) tc);

			case NEGATION:
				return containsPlaceholder(((NegationCriterion) tc).getCriterion());

			case PLACEHOLDER:
				return true;

			default:
				return false;
		}
	}

	public static boolean containsPlaceholder(JunctionCriterion jtc) {
		for (TraversingCriterion tc : nullSafe(jtc.getCriteria()))
			if (containsPlaceholder(tc))
				return true;
		return false;
	}

}
