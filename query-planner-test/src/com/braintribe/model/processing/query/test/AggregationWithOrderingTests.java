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
package com.braintribe.model.processing.query.test;

import static com.braintribe.model.query.OrderingDirection.descending;

import org.junit.Test;

import com.braintribe.model.processing.query.test.model.Person;
import com.braintribe.model.query.SelectQuery;
import com.braintribe.model.queryplan.set.AggregatingProjection;
import com.braintribe.model.queryplan.set.OrderedSet;
import com.braintribe.model.queryplan.set.Projection;
import com.braintribe.model.queryplan.set.SortCriterion;
import com.braintribe.model.queryplan.value.AggregationFunctionType;

/**
 * 
 */
public class AggregationWithOrderingTests extends AbstractQueryPlannerTests {

	/** Ordering by a component that was grouped by (p.name)  */
	@Test
	public void orderByGrouped_Selected() {
		// @formatter:off
		SelectQuery selectQuery = query()
				.select("p", "name")
				.select().count("p", "age")
				.from(Person.T, "p")
				.orderBy()
					.property("p", "name")
				.done();
		// @formatter:on

		runTest(selectQuery);

		// @formatter:off
		assertQueryPlan()
		.hasType(OrderedSet.T)
			.whereOperand()
			.hasType(AggregatingProjection.T)
				.whereOperand().isSourceSet_(Person.T)
				.hasValues(2)
					.whereElementAt(0).isValueProperty_("name")
					.whereElementAt(1)
						.isAggregateFunction(AggregationFunctionType.count)
						.whereOperand()				
							.isValueProperty("age")
								.whereValue().isTupleComponent_(0)
							.close()
						.close()
					.close()
				.close()
			.whereProperty("sortCriteria")
			.whereElementAt(0)
				.hasType(SortCriterion.T)
					.whereProperty("descending").isFalse_()
					.whereValue().isTupleComponent_(0)

		;
		// @formatter:on
	}

	/** Ordering by a component that was grouped by (p.name)  */
	@Test
	public void orderByGrouped_NotSelected() {
		// @formatter:off
		SelectQuery selectQuery = query()
				.select("p", "name")
				.select().count("p", "age")
				.from(Person.T, "p")
				.orderBy()
					.property("p", "companyName")
				.done();
		// @formatter:on

		runTest(selectQuery);

		// @formatter:off
		assertQueryPlan()
		.hasType(Projection.T)
			.whereOperand()
				.hasType(OrderedSet.T)
				.whereOperand()
					.hasType(AggregatingProjection.T)
					.whereOperand().isSourceSet_(Person.T)
					.hasValues(3)
						.whereElementAt(0).isValueProperty_("name")
						.whereElementAt(1)
							.isAggregateFunction(AggregationFunctionType.count)
							.whereOperand()				
								.isValueProperty("age")
								.whereValue().isTupleComponent_(0)
							.close() // whereOperand
						.close() // values[1]
						.whereElementAt(2).isValueProperty_("companyName")
					.close() // AggregatingProjection.values
				.close() // OrderedSet.operand
				.whereProperty("sortCriteria")
					.whereElementAt(0)
						.hasType(SortCriterion.T)
						.whereProperty("descending").isFalse_()
						.whereValue().isTupleComponent_(2)
					.close() // sortCriteria[0]
				.close() // OrdereSet.sortCriteria
			.close() // Projection.operand
			.hasValues(2)
				.whereElementAt(0).isTupleComponent_(0)
				.whereElementAt(1).isTupleComponent_(1)
		;
		// @formatter:on
	}

	/** Straight forward case where we are selecting by the aggregate function we are selecting - count(p.age) */
	@Test
	public void orderByAggregation_Selected() {
		// @formatter:off
		SelectQuery selectQuery = query()
				.select("p", "name")
				.select().count("p", "age")
				.from(Person.T, "p")
				.orderBy()
					.count("p", "age")
				.done();
		// @formatter:on

		runTest(selectQuery);

		// @formatter:off
		assertQueryPlan()
		.hasType(OrderedSet.T)
			.whereOperand()
			.hasType(AggregatingProjection.T)
				.whereOperand().isSourceSet_(Person.T)
				.hasValues(2)
					.whereElementAt(0).isValueProperty_("name")
					.whereElementAt(1)
						.isAggregateFunction(AggregationFunctionType.count)
						.whereOperand()				
							.isValueProperty("age")
								.whereValue().isTupleComponent_(0)
							.close()
						.close()
					.close()
				.close()
			.whereProperty("sortCriteria")
			.whereElementAt(0)
				.hasType(SortCriterion.T)
					.whereProperty("descending").isFalse_()
					.whereValue().isTupleComponent_(1)

		;
		// @formatter:on
	}

	/** We are selecting multiple aggregations count(p.age), max(p.age) */
	@Test
	public void orderByAggregation_Selected_Multi() {
		// @formatter:off
		SelectQuery selectQuery = query()
				.select("p", "name")
				.select().count("p", "age")
				.select().max("p", "age")
				.from(Person.T, "p")
				.orderByCascade()
					.count("p", "age")
					.dir(descending).max("p", "age")
				.close()
				.done();
		// @formatter:on

		runTest(selectQuery);

		// @formatter:off
		assertQueryPlan()
		.hasType(OrderedSet.T)
			.whereOperand()
			.hasType(AggregatingProjection.T)
				.whereOperand().isSourceSet_(Person.T)
				.hasValues(3)
					.whereElementAt(0).isValueProperty_("name")
					.whereElementAt(1)
						.isAggregateFunction(AggregationFunctionType.count)
						.whereOperand()				
							.isValueProperty("age").whereValue().isTupleComponent_(0)
						.close()
					.close()
					.whereElementAt(2)
						.isAggregateFunction(AggregationFunctionType.max)
						.whereOperand()				
							.isValueProperty("age").whereValue().isTupleComponent_(0)
						.close()
					.close()
				.close()
			.close()
			.whereProperty("sortCriteria")
				.whereElementAt(0)
					.hasType(SortCriterion.T)
						.whereProperty("descending").isFalse_()
						.whereValue().isTupleComponent_(1)
					.close()
				.whereElementAt(1)
					.hasType(SortCriterion.T)
						.whereProperty("descending").isTrue_()
						.whereValue().isTupleComponent_(2)
					.close()
		;
		// @formatter:on
	}

	/** We are selecting count(p.age) but ordering by sum(p.age), so it's a little trickier. */
	@Test
	public void orderByAggregation_NotSelected() {
		// @formatter:off
		SelectQuery selectQuery = query()
				.select("p", "name")
				.select().count("p", "age")
				.from(Person.T, "p")
				.orderBy()
					.sum("p", "age")
				.done();
		// @formatter:on

		runTest(selectQuery);

		// @formatter:off
		assertQueryPlan()
		.hasType(Projection.T)
			.whereOperand()
				.hasType(OrderedSet.T)
				.whereOperand()
					.hasType(AggregatingProjection.T)
					.whereOperand().isSourceSet_(Person.T)
					.hasValues(3)
						.whereElementAt(0).isValueProperty_("name")
						.whereElementAt(1)
							.isAggregateFunction(AggregationFunctionType.count)
							.whereOperand()				
								.isValueProperty("age")
								.whereValue().isTupleComponent_(0)
							.close() // whereOperand
						.close() // values[2]
						.whereElementAt(2)
							.isAggregateFunction(AggregationFunctionType.sum)
							.whereOperand()				
								.isValueProperty("age")
								.whereValue().isTupleComponent_(0)
							.close() // whereOperand
						.close() // values[2]
					.close() // AggregatingProjection.values
				.close() // OrderedSet.operand
				.whereProperty("sortCriteria")
					.whereElementAt(0)
						.hasType(SortCriterion.T)
						.whereProperty("descending").isFalse_()
						.whereValue().isTupleComponent_(2)
					.close() // sortCriteria[0]
				.close() // OrdereSet.sortCriteria
			.close() // Projection.operand
			.hasValues(2)
				.whereElementAt(0).isTupleComponent_(0)
				.whereElementAt(1).isTupleComponent_(1)
		;
		// @formatter:on
	}
}
