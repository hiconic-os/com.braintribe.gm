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
package com.braintribe.model.processing.query.planner.builder;

import static com.braintribe.utils.lcd.CollectionTools2.first;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.braintribe.model.processing.query.planner.context.QueryPlannerContext;
import com.braintribe.model.queryplan.filter.Condition;
import com.braintribe.model.queryplan.filter.Conjunction;
import com.braintribe.model.queryplan.filter.Contains;
import com.braintribe.model.queryplan.filter.Disjunction;
import com.braintribe.model.queryplan.filter.Equality;
import com.braintribe.model.queryplan.filter.FullText;
import com.braintribe.model.queryplan.filter.GreaterThan;
import com.braintribe.model.queryplan.filter.GreaterThanOrEqual;
import com.braintribe.model.queryplan.filter.ILike;
import com.braintribe.model.queryplan.filter.In;
import com.braintribe.model.queryplan.filter.Junction;
import com.braintribe.model.queryplan.filter.LessThan;
import com.braintribe.model.queryplan.filter.LessThanOrEqual;
import com.braintribe.model.queryplan.filter.Like;
import com.braintribe.model.queryplan.filter.Negation;
import com.braintribe.model.queryplan.filter.Unequality;
import com.braintribe.model.queryplan.filter.ValueComparison;
import com.braintribe.model.queryplan.value.Value;

/**
 * Just a helper class for creating instances of query-plan conditions.
 */
public class ConditionBuilder {

	public static Condition condition(Collection<com.braintribe.model.query.conditions.Condition> conditions, QueryPlannerContext context) {
		List<com.braintribe.model.queryplan.filter.Condition> operands = conditions.stream() //
				.map(context::convertCondition) //
				.collect(Collectors.toList());

		return operands.size() == 1 ? first(operands) : newConjunction(operands);
	}

	public static Conjunction newConjunction() {
		return Conjunction.T.create();
	}

	public static Conjunction newConjunction(List<Condition> operands) {
		return newJunction(newConjunction(), operands);
	}

	public static Disjunction newDisjunction() {
		return Disjunction.T.create();
	}

	public static Disjunction newDisjunction(List<Condition> operands) {
		return newJunction(newDisjunction(), operands);
	}

	private static <J extends Junction> J newJunction(J junction, List<Condition> operands) {
		junction.setOperands(operands);

		return junction;
	}

	public static Negation newNegation(Condition operand) {
		Negation result = Negation.T.create();
		result.setOperand(operand);

		return result;
	}

	public static FullText fullText(int entityIndex, String text) {
		FullText fullText = FullText.T.create();

		fullText.setLeftOperand(ValueBuilder.tupleComponent(entityIndex));
		fullText.setRightOperand(ValueBuilder.staticValue(text));

		return fullText;

	}

	public static Contains newContains(Value left, Value right) {
		return newValueComparison(Contains.T.create(), left, right);
	}

	public static Equality newEquality(Value left, Value right) {
		return newValueComparison(Equality.T.create(), left, right);
	}

	public static GreaterThan newGreaterThan(Value left, Value right) {
		return newValueComparison(GreaterThan.T.create(), left, right);
	}

	public static GreaterThanOrEqual newGreaterThanOrEqual(Value left, Value right) {
		return newValueComparison(GreaterThanOrEqual.T.create(), left, right);
	}

	public static ILike newILike(Value left, Value right) {
		return newValueComparison(ILike.T.create(), left, right);
	}

	public static In newIn(Value left, Value right) {
		return newValueComparison(In.T.create(), left, right);
	}

	public static LessThan newLessThan(Value left, Value right) {
		return newValueComparison(LessThan.T.create(), left, right);
	}

	public static LessThanOrEqual newLessThanOrEqual(Value left, Value right) {
		return newValueComparison(LessThanOrEqual.T.create(), left, right);
	}

	public static Like newLike(Value left, Value right) {
		return newValueComparison(Like.T.create(), left, right);
	}

	public static Unequality newUnequality(Value left, Value right) {
		return newValueComparison(Unequality.T.create(), left, right);
	}

	private static <C extends ValueComparison> C newValueComparison(C c, Value left, Value right) {
		c.setLeftOperand(left);
		c.setRightOperand(right);

		return c;
	}

}
