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
package com.braintribe.model.processing.query.planner.core.property;

import static com.braintribe.utils.lcd.CollectionTools2.newSet;

import java.util.Set;

import com.braintribe.model.processing.query.planner.RuntimeQueryPlannerException;
import com.braintribe.model.processing.query.planner.context.QueryPlannerContext;
import com.braintribe.model.processing.query.planner.context.QuerySourceManager;
import com.braintribe.model.query.Join;
import com.braintribe.model.queryplan.filter.Condition;
import com.braintribe.model.queryplan.filter.Junction;
import com.braintribe.model.queryplan.filter.Negation;
import com.braintribe.model.queryplan.filter.ValueComparison;
import com.braintribe.model.queryplan.index.GeneratedMetricIndex;
import com.braintribe.model.queryplan.value.QueryFunctionValue;
import com.braintribe.model.queryplan.value.TupleComponent;
import com.braintribe.model.queryplan.value.Value;
import com.braintribe.model.queryplan.value.ValueProperty;
import com.braintribe.model.queryplan.value.range.Range;
import com.braintribe.model.queryplan.value.range.RangeIntersection;
import com.braintribe.model.queryplan.value.range.SimpleRange;

/**
 * Find all the joins that need to be done to evaluate given condition or value.
 */
public class JoinFinder {

	private final QueryPlannerContext context;
	private final QuerySourceManager sourceManager;

	private Set<Join> joins;

	public JoinFinder(QueryPlannerContext context) {
		this.context = context;
		this.sourceManager = context.sourceManager();
	}

	public Set<Join> findRequiredJoins(Condition condition) {
		joins = newSet();
		addJoins(condition);

		return joins;
	}

	public Set<Join> findRequiredJoins(GeneratedMetricIndex index) {
		return findRequiredJoins(index.getIndexKey());
	}

	public Set<Join> findRequiredJoins(Range range) {
		joins = newSet();
		addJoins(range);

		return joins;
	}

	public Set<Join> findRequiredJoins(Value value) {
		joins = newSet();
		addJoins(value);

		return joins;
	}

	// ###################################
	// ## . . . . . Condition . . . . . ##
	// ###################################

	private void addJoins(Condition condition) {
		switch (condition.conditionType()) {
			case conjunction:
			case disjunction:
				addJoins((Junction) condition);
				return;

			case negation:
				addJoins((Negation) condition);
				return;

			case contains:
			case equality:
			case fullText:
			case greater:
			case greaterOrEqual:
			case ilike:
			case in:
			case instanceOf:
			case less:
			case lessOrEqual:
			case like:
			case unequality:
				addJoins((ValueComparison) condition);
				return;
		}

		throw new RuntimeQueryPlannerException("Unsupported condition: " + condition + " of type: " + condition.conditionType());
	}

	private void addJoins(Junction condition) {
		for (Condition c: condition.getOperands()) {
			addJoins(c);
		}
	}

	private void addJoins(Negation condition) {
		addJoins(condition.getOperand());
	}

	private void addJoins(ValueComparison condition) {
		addJoins(condition.getLeftOperand());
		addJoins(condition.getRightOperand());
	}

	// ###################################
	// ## . . . . . . Range . . . . . . ##
	// ###################################

	private void addJoins(Range range) {
		switch (range.rangeType()) {
			case intersection:
				addJoins((RangeIntersection) range);
				return;
			case simple:
				addJoins((SimpleRange) range);
				return;

		}

		throw new RuntimeQueryPlannerException("Unsupported range: " + range + " of type: " + range.rangeType());
	}

	private void addJoins(RangeIntersection range) {
		for (SimpleRange sr: range.getRanges()) {
			addJoins(sr);
		}
	}

	private void addJoins(SimpleRange range) {
		addJoins(range.getLowerBound());
		addJoins(range.getUpperBound());
	}

	// ###################################
	// ## . . . . . . Value . . . . . . ##
	// ###################################

	private void addJoins(Value value) {
		if (value == null) {
			return;
		}

		switch (value.valueType()) {
			case aggregateFunction:
				throw new RuntimeQueryPlannerException("Aggregate function not expected here! Probably an illegal QueryPlan.");

			case queryFunction:
				addJoins((QueryFunctionValue) value);
				return;

			case hashSetProjection:
				throw new RuntimeQueryPlannerException("HashSetProjection is not supported right now!");

			case staticValue:
				return;

			case tupleComponent:
				addJoins((TupleComponent) value);
				return;

			case valueProperty:
				addJoins((ValueProperty) value);
				return;
			default:
				throw new RuntimeQueryPlannerException("Unsupported value: " + value + " of type: " + value.valueType());
		}
	}

	private void addJoins(QueryFunctionValue value) {
		for (Value operandValue: context.noticeQueryFunction(value.getQueryFunction()).values()) {
			addJoins(operandValue);
		}
	}

	private void addJoins(TupleComponent value) {
		int index = value.getTupleComponentIndex();

		Join join = sourceManager.joinForIndex(index);

		// TODO <LOW PRIO> if other component is suspicious - checking null or something, we want to add the entire
		// chain

		if (join != null) {
			joins.add(join);
		}

		Set<Join> rightJoins = sourceManager.rightJoinsForIndex(index);
		if (rightJoins != null) {
			joins.addAll(rightJoins);
		}

	}

	private void addJoins(ValueProperty value) {
		addJoins(value.getValue());
	}

}
