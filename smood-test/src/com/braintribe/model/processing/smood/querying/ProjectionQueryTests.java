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
import com.braintribe.model.query.SelectQuery;

/**
 * 
 */
public class ProjectionQueryTests extends AbstractSelectQueryTests {

	/** SeeProjectionTests#selectingEntityAndProperty() */
	@Test
	public void selectingEntityAndProperty() {
		Person p1 = b.person("p1").create();
		Person p2 = b.person("p2").create();

		// @formatter:off
		SelectQuery selectQuery = query()
				.from(Person.T, "person")
				.select("person")
				.select("person", "name")
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertResultContains(p1, "p1");
		assertResultContains(p2, "p2");
		assertNoMoreResults();
	}

	/** SeeProjectionTests#selectingCompoundProperty() */
	@Test
	public void selectingCompoundProperty() {
		Company c1 = b.company("C1").create();
		Company c2 = b.company("C2").create();

		Person p1 = b.owner("p1").company(c1).create();
		Person p2 = b.owner("p2").company(c2).create();

		// @formatter:off
		SelectQuery selectQuery = query()
				.from(Owner.T, "person")
				.select("person")
				.select("person", "company.name")
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertResultContains(p1, "C1");
		assertResultContains(p2, "C2");
		assertNoMoreResults();
	}

	/** SeeProjectionTests#selectingConstants() */
	@Test
	public void selectingConstants() {
		Person p1 = b.person("p1").create();
		Person p2 = b.person("p2").create();

		// @formatter:off
		SelectQuery selectQuery = query()
				.from(Person.T, "person")
				.select("person")
				.select().value(99L)
				.select().value("constantString")
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertResultContains(p1, 99L, "constantString");
		assertResultContains(p2, 99L, "constantString");
		assertNoMoreResults();
	}

	/** SeeProjectionTests#selectingLocalizedValue() */
	@Test
	public void selectingLocalizedValue() {
		Person p1 = b.person("p1").localizedString("en", "yes", "pt", "sim").create();
		Person p2 = b.person("p2").localizedString("en", "good", "pt", "bom").create();

		// @formatter:off
		SelectQuery selectQuery = query()
				.from(Person.T, "person")
				.select("person")
				.select().localize("pt").property("person", "localizedString")
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertResultContains(p1, "sim");
		assertResultContains(p2, "bom");
	}

	/** SeeProjectionTests#selectingMapKey() */
	@Test
	public void selectingMapKey() {
		Company c1 = b.company("C1").create();
		Company c2 = b.company("C2").create();

		b.owner("p").addToCompanyMap("c1", c1).addToCompanyMap("c2", c2).create();

		// @formatter:off
		SelectQuery selectQuery = query()
				.from(Owner.T, "person")
					.join("person", "companyMap", "cs")
				.select().mapKey("cs")
				.select("cs")
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertResultContains("c1", c1);
		assertResultContains("c2", c2);
	}

	/** SeeProjectionTests#selectingMapKey() */
	@Test
	public void selectingMapValue() {
		Company c1 = b.company("C1").create();
		Company c2 = b.company("C2").create();

		b.owner("p").addToCompanyMap("c1", c1).addToCompanyMap("c2", c2).create();

		// @formatter:off
		SelectQuery selectQuery = query()
				.from(Owner.T, "person")
				.select("person", "name")
				.select("person", "companyMap")
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertResultContains("p", c1);
		assertResultContains("p", c2);
	}

}
