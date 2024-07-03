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

import org.junit.Test;

import com.braintribe.model.processing.query.test.model.Person;
import com.braintribe.model.query.SelectQuery;
import com.braintribe.model.queryplan.filter.GreaterThanOrEqual;
import com.braintribe.model.queryplan.set.AggregatingProjection;
import com.braintribe.model.queryplan.set.FilteredSet;
import com.braintribe.model.queryplan.set.Projection;
import com.braintribe.model.queryplan.value.AggregationFunctionType;

/**
 * 
 */
public class AggregationWithHavingTests extends AbstractQueryPlannerTests {

	@Test
	public void conditionOnSelected() {
		// @formatter:off
		SelectQuery selectQuery = query()
				.select("p", "name")
				.select().count("p", "age")
				.from(Person.T, "p")
				.having()
					.count("p", "age").ge(2)
				.done();
		// @formatter:on

		runTest(selectQuery);

		// @formatter:off
		assertQueryPlan()
			.hasType(FilteredSet.T)
			.whereOperand()
				.hasType(AggregatingProjection.T)
				.whereOperand().isSourceSet_(Person.T)
				.hasValues(2)
					.whereElementAt(0).isValueProperty_("name")
					.whereElementAt(1)
						.isAggregateFunction(AggregationFunctionType.count)
						.whereOperand()				
							.isValueProperty("age").whereValue().isTupleComponent_(0)
						.close()
					.close()
				.close()
			.close()
			.whereProperty("filter")
				.hasType(GreaterThanOrEqual.T)
				.whereProperty("leftOperand").isTupleComponent_(1)
				.whereProperty("rightOperand").isStaticValue_(2)
		;
		// @formatter:on
	}

	@Test
	public void conditionOnNotSelected() {
		// @formatter:off
		SelectQuery selectQuery = query()
				.select("p", "name")
				.select().count("p", "age")
				.from(Person.T, "p")
				.having()
					.sum("p", "age").ge(50)
				.done();
		// @formatter:on

		runTest(selectQuery);

		// @formatter:off
		assertQueryPlan()
			.hasType(Projection.T)
			.whereOperand()
				.hasType(FilteredSet.T)
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
							.isAggregateFunction(AggregationFunctionType.sum)
							.whereOperand()				
								.isValueProperty("age").whereValue().isTupleComponent_(0)
							.close()
						.close()
					.close() // AggregatingProjection.values
				.close() // FilteredSet.operand
				.whereProperty("filter")
					.hasType(GreaterThanOrEqual.T)
					.whereProperty("leftOperand").isTupleComponent_(2)
					.whereProperty("rightOperand").isStaticValue_(50)
				.close()
			.close()
			.hasValues(2)
		;
		// @formatter:on
	}
}
