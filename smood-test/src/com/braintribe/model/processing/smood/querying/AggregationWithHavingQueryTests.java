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
package com.braintribe.model.processing.smood.querying;

import org.junit.Test;

import com.braintribe.model.processing.query.test.model.Person;
import com.braintribe.model.query.SelectQuery;

/**
 * 
 */
public class AggregationWithHavingQueryTests extends AbstractSelectQueryTests {

	/** SeeAggregationWithHavingTests#conditionOnSelected() */
	@Test
	public void conditionOnSelected() {
		b.person("p1").age(10).create();
		b.person("p2").age(20).create();
		b.person("p2").age(30).create();
		b.person("p3").age(40).create();
		b.person("p3").age(50).create();
		b.person("p3").age(60).create();

		// @formatter:off
		SelectQuery selectQuery = query()
				.select("p", "name")
				.select().count("p", "age")
				.from(Person.T, "p")
				.having()
					.count("p", "age").ge(2L)
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertResultContains("p3", 3L);
		assertResultContains("p2", 2L);
		assertNoMoreResults();
	}

	/** SeeAggregationWithHavingTests#conditionOnNotSelected() */
	@Test
	public void conditionOnNotSelected() {
		b.person("p1").age(10).create();
		b.person("p2").age(20).create();
		b.person("p2").age(30).create();
		b.person("p3").age(40).create();
		b.person("p3").age(50).create();
		b.person("p3").age(60).create();

		// @formatter:off
		SelectQuery selectQuery = query()
				.select("p", "name")
				.select().count("p", "age")
				.from(Person.T, "p")
				.having()
					.sum("p", "age").ge(50)
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertResultContains("p3", 3L);
		assertResultContains("p2", 2L);
		assertNoMoreResults();
	}

}
