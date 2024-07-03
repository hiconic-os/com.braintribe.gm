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

import com.braintribe.model.processing.query.test.model.Color;
import com.braintribe.model.processing.query.test.model.Company;
import com.braintribe.model.processing.query.test.model.Owner;
import com.braintribe.model.processing.query.test.model.Person;
import com.braintribe.model.query.SelectQuery;

/**
 * 
 */
public class CollectionRelatedQueryTests extends AbstractSelectQueryTests {

	/** SeeCollectionRelatedTests#selectingCollection_Directly() */
	@Test
	public void selectingEmptyCollection_Directly() {
		b.person("robert").create();
		b.person("william").create();

		// @formatter:off
		SelectQuery selectQuery = query()
				.select("n")
				.from(Person.T, "person")
					.join("person", "nicknames", "n")
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertNoMoreResults();
	}

	@Test
	public void selectingEmptyCollection_Directly_LeftJoin() {
		b.person("robert").create();
		b.person("william").create();

		// @formatter:off
		SelectQuery selectQuery = query()
				.select("person", "nicknames")
				.from(Person.T, "person")
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertResultContains(null);
		assertResultContains(null);
		assertNoMoreResults();
	}

	/** SeeCollectionRelatedTests#selectingCollection_Directly() */
	@Test
	public void selectingCollection_Directly() {
		b.person("robert").nicknames("rob", "bob").create();
		b.person("william").nicknames("will", "bill").create();

		// @formatter:off
		SelectQuery selectQuery = query()
				.select("person", "nicknames")
				.from(Person.T, "person")
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertResultContains("rob");
		assertResultContains("bob");
		assertResultContains("will");
		assertResultContains("bill");
		assertNoMoreResults();
	}

	/** SeeCollectionRelatedTests#selectingCollection_ByJoining() */
	@Test
	public void selectingCollection_ByJoining() {
		b.person("robert").nicknames("rob", "bob").create();
		b.person("william").nicknames("will", "bill").create();

		// @formatter:off
		SelectQuery selectQuery = query()
				.select("n")
				.from(Person.T, "p")
					.join("p", "nicknames", "n")
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertResultContains("rob");
		assertResultContains("bob");
		assertResultContains("will");
		assertResultContains("bill");
		assertNoMoreResults();
	}

	/** SeeCollectionRelatedTests#selectingCollection_DirectlyAndByJoining() */
	@Test
	public void selectingCollection_DirectlyAndByJoining() {
		b.person("robert").nicknames("rob", "bob").create();
		b.person("william").nicknames("will", "bill").create();

		// @formatter:off
		SelectQuery selectQuery = query()
				.select("p", "nicknames")
				.from(Person.T, "p")
					.join("p", "nicknames", "n")
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertResultContains("rob");
		assertResultContains("bob");
		assertResultContains("will");
		assertResultContains("bill");

		assertResultContains("rob");
		assertResultContains("bob");
		assertResultContains("will");
		assertResultContains("bill");

		assertNoMoreResults();
	}

	private void assertContainsAllCombinations(String[] nicks) {
		for (String n1 : nicks) {
			for (String n2 : nicks) {
				assertResultContains(n1, n2);
			}
		}
	}

	/** SeeCollectionRelatedTests#selectingCollection_DirectlyTwiceCausingCartesianProduct() */
	@Test
	public void selectingCollection_DirectlyTwiceCausingCartesianProduct() {
		b.person("robert").nicknames("rob", "bob").create();
		b.person("william").nicknames("will", "bill").create();

		// @formatter:off
		SelectQuery selectQuery = query()
				.select("p", "nicknames")
				.select("p", "nicknames")
				.from(Person.T, "p")
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertContainsAllCombinations(new String[] { "rob", "bob" });
		assertContainsAllCombinations(new String[] { "will", "bill" });
		assertNoMoreResults();
	}

	/** SeeCollectionRelatedTests#selectingCollectionUsingInCondition() */
	@Test
	public void selectingCollectionUsingInCondition() {
		b.person("robert").nicknames("rob", "bob").create();
		b.person("william").nicknames("will", "bill").create();

		// @formatter:off
		SelectQuery selectQuery = query()
				.from(Person.T, "p")
					.select("p", "name")
					.where()
						.value("bob").in().property("p", "nicknames")
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertResultContains("robert");
		assertNoMoreResults();
	}

	@Test
	public void queryEntitiesInCondition_WithEnums() {
		Company c = b.company("C1").colors(Color.BLUE).create();

		// @formatter:off
		SelectQuery selectQuery = query()
					.select("c")
					.from(Company.T, "c")
					.where()
						.value(Color.BLUE).in().property("c", "colors")
					.done();
		// @formatter:on

		evaluate(selectQuery);

		assertResultContains(c);
		assertNoMoreResults();
	}

	/** SeeCollectionRelatedTests#joiningWithConditionOnMapKey() */
	@Test
	public void joiningWithConditionOnMapKey() {
		/* ignored = */ b.company("Not Part Of Result").create();
		Company cheap = b.company("Cheap Company").create();
		Company expensive = b.company("Expensive Company").create();

		b.owner("robert").addToCompanyValueMap(cheap, 10).addToCompanyValueMap(expensive, 1000).create();

		// @formatter:off
		SelectQuery selectQuery = query()
				.from(Owner.T, "o")
					.join("o", "companyValueMap", "map")
				.from(Company.class, "c")
				.select("c", "name")
				.where()
					.mapKey("map").eq().entity("c")
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertResultContains(cheap.getName());
		assertResultContains(expensive.getName());
		assertNoMoreResults();
	}

}
