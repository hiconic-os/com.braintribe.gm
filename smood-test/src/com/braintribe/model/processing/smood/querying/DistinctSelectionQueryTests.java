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

import com.braintribe.model.processing.query.test.model.Company;
import com.braintribe.model.processing.query.test.model.Owner;
import com.braintribe.model.processing.query.test.model.Person;
import com.braintribe.model.query.OrderingDirection;
import com.braintribe.model.query.SelectQuery;

/**
 * 
 */
public class DistinctSelectionQueryTests extends AbstractSelectQueryTests {

	@Test
	public void simpleProperty() {
		b.person("p1").create();
		b.person("p1").create();
		b.person("p2").create();
		b.person("p2").create();
		b.person("p2").create();

		// @formatter:off
		SelectQuery selectQuery = query()
				.select("p", "name")
				.from(Person.T, "p")
				.distinct()
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertResultContains("p1");
		assertResultContains("p2");
		assertNoMoreResults();
	}

	@Test
	public void simpleProperty_OrderedByOtherProperty() {
		b.person("p1").age(1).create();
		b.person("p1").age(2).create(); // third
		b.person("p2").age(3).create();
		b.person("p2").age(5).create(); // first
		b.person("p3").age(4).create(); // second

		// @formatter:off
		SelectQuery selectQuery = query()
				.select("p", "name")
				.distinct()
				.from(Person.T, "p")
				.orderBy(OrderingDirection.descending).property("p", "age")
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertNextResult("p2");
		assertNextResult("p3");
		assertNextResult("p1");
		assertNoMoreResults();
	}

	@Test
	public void listProperty() {
		Company c1 = b.company("C1").create();
		Company c2 = b.company("C2").create();

		b.owner("John").addToCompanyList(c1, c1, c2, c2, c1).create();

		// @formatter:off
		SelectQuery selectQuery = query()
				.select("o", "companyList")
				.from(Owner.T, "o")
				.distinct()
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertResultContains(c1);
		assertResultContains(c2);
		assertNoMoreResults();
	}

	@Test
	public void setProperty() {
		b.person("p1").nicknames("n1").create();
		b.person("p1").nicknames("n1", "n2").create();
		b.person("p1").nicknames("n2").create();

		// @formatter:off
		SelectQuery selectQuery = query()
				.select("p", "nicknames")
				.from(Person.T, "p")
				.distinct()
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertResultContains("n1");
		assertResultContains("n2");
		assertNoMoreResults();
	}

}
