// ============================================================================
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
// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
// ============================================================================
package com.braintribe.model.processing.smood.querying;

import static com.braintribe.model.query.OrderingDirection.descending;

import org.junit.Test;

import com.braintribe.model.processing.query.test.model.Person;
import com.braintribe.model.query.SelectQuery;

/**
 * 
 */
public class AggregationWithOrderingQueryTests extends AbstractSelectQueryTests {

	/** SeeAggregationWithOrderingTests#orderByGrouped_Selected() */
	@Test
	public void orderByGrouped_Selected() {
		b.person("andy").age(10).create();
		b.person("andy").age(20).create();
		b.person("bob").age(30).create();
		b.person("bob").age(40).create();
		b.person("bob").age(50).create();
		b.person("dwight").age(36).create();
		b.person("erin").age(28).create();
		b.person("phyllis").age(44).create();

		// @formatter:off
		SelectQuery selectQuery = query()
				.select("p", "name")
				.select().count("p", "age")
				.from(Person.T, "p")
				.orderBy()
					.property("p", "name")
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertNextResult("andy", 2L);
		assertNextResult("bob", 3L);
		assertNextResult("dwight", 1L);
		assertNextResult("erin", 1L);
		assertNextResult("phyllis", 1L);
		assertNoMoreResults();
	}


	/** SeeAggregationWithOrderingTests#orderByGrouped_NotSelected() */
	@Test
	public void orderByGrouped_NotSelected() {
		b.person("andy").indexedName("z").age(10).create();
		b.person("andy").indexedName("z").age(20).create();
		b.person("bob").indexedName("y").age(30).create();
		b.person("bob").indexedName("y").age(40).create();
		b.person("bob").indexedName("y").age(50).create();
		b.person("dwight").indexedName("x").age(36).create();
		b.person("erin").indexedName("w").age(28).create();
		b.person("phyllis").indexedName("v").age(44).create();

		// @formatter:off
		SelectQuery selectQuery = query()
				.select("p", "name")
				.select().count("p", "age")
				.from(Person.T, "p")
				.orderBy()
					.property("p", "indexedName")
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertNextResult("phyllis", 1L);
		assertNextResult("erin", 1L);
		assertNextResult("dwight", 1L);
		assertNextResult("bob", 3L);
		assertNextResult("andy", 2L);
		assertNoMoreResults();
	}

	/** SeeAggregationWithOrderingTests#orderByAggregation_Selected() */
	@Test
	public void orderByAggregation_Selected() {
		b.person("andy").age(10).create();
		b.person("andy").age(20).create();
		b.person("bob").age(30).create();
		b.person("bob").age(40).create();
		b.person("bob").age(50).create();
		b.person("dwight").age(36).create();

		// @formatter:off
		SelectQuery selectQuery = query()
				.select("p", "name")
				.select().count("p", "age")
				.from(Person.T, "p")
				.orderBy()
					.count("p", "age")
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertNextResult("dwight", 1L);
		assertNextResult("andy", 2L);
		assertNextResult("bob", 3L);
	}

	/** SeeAggregationWithOrderingTests#orderByAggregation_Selected_Multi() */
	@Test
	public void orderByAggregation_Selected_Multi() {
		b.person("andy").age(15).create();
		b.person("andy").age(20).create();
		b.person("andy").age(25).create();
		b.person("bob").age(35).create();
		b.person("bob").age(40).create();
		b.person("bob").age(45).create();
		b.person("dwight").age(36).create();

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

		evaluate(selectQuery);

		assertNextResult("dwight", 1L, 36);
		assertNextResult("bob", 3L, 45);
		assertNextResult("andy", 3L, 25);
	}

	/** SeeAggregationWithOrderingTests#orderByAggregation_NotSelected() */
	@Test
	public void orderByAggregation_NotSelected() {
		b.person("andy").age(100).create();
		b.person("andy").age(20).create();
		b.person("bob").age(30).create();
		b.person("bob").age(35).create();
		b.person("bob").age(40).create();
		b.person("dwight").age(36).create();

		// @formatter:off
		SelectQuery selectQuery = query()
				.select("p", "name")
				.select().count("p", "age")
				.from(Person.T, "p")
				.orderBy()
					.sum("p", "age")
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertNextResult("dwight", 1L); // 36
		assertNextResult("bob", 3L); // 105
		assertNextResult("andy", 2L); // 120 
	}
}
