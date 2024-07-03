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
import com.braintribe.model.queryplan.set.AggregatingProjection;
import com.braintribe.model.queryplan.value.AggregationFunctionType;
import com.braintribe.model.queryplan.value.TupleComponent;

/**
 * 
 */
public class AggregationTests extends AbstractQueryPlannerTests {

	@Test
	public void selectingCountOnly() {
		// @formatter:off
		SelectQuery selectQuery = query()
				.select().count("p", null)
				.from(Person.T, "p")
				.done();
		// @formatter:on

		runTest(selectQuery);

		// @formatter:off
		assertQueryPlan()
		.hasType(AggregatingProjection.T)
			.whereOperand().isSourceSet_(Person.T)
			.hasValues(1)
				.whereElementAt(0)
					.isAggregateFunction(AggregationFunctionType.count)
					.whereOperand()		
						.isTupleComponent_(0)
		;
		// @formatter:on
	}

	@Test
	public void selectingCountAndEntity() {
		// @formatter:off
		SelectQuery selectQuery = query()
				.select("p")
				.select().count("p", null)
				.from(Person.T, "p")
				.done();
		// @formatter:on

		runTest(selectQuery);

		// @formatter:off
		assertQueryPlan()
		.hasType(AggregatingProjection.T)
			.whereOperand().isSourceSet_(Person.T)
			.hasValues(2)
				.whereElementAt(0).hasType(TupleComponent.T).close()
				.whereElementAt(1)
					.isAggregateFunction(AggregationFunctionType.count)
					.whereOperand()
						.isTupleComponent_(0)				
		;
		// @formatter:on
	}

	@Test
	public void countEntityProperty() {
		// @formatter:off
		SelectQuery selectQuery = query()
				.select("p")
				.select().count("p", "company")
				.from(Person.T, "p")
				.done();
		// @formatter:on

		runTest(selectQuery);

		// @formatter:off
		assertQueryPlan()
		.hasType(AggregatingProjection.T)
			.whereOperand().isSourceSet_(Person.T)
			.hasValues(2)
				.whereElementAt(0).isTupleComponent_(0)
				.whereElementAt(1)
					.isAggregateFunction(AggregationFunctionType.count)
					.whereOperand()				
						.isValueProperty("company")
						.whereValue().isTupleComponent_(0)
		;
		// @formatter:on
	}

}
