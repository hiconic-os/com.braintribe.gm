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
import com.braintribe.model.processing.query.test.model.Rectangle;
import com.braintribe.model.query.SelectQuery;

/**
 * 
 */
public class ConstantConditionSelectQueryTests extends AbstractSelectQueryTests {
	private static final String MATCH_ALL = "";

	/* We had a bug, that the condition was evaluated as true iff there was a String property with non-null value having
	 * given string as substring. But (for now) we want a full-text with empty string to match everything, even if there
	 * is no string property. */
	@Test
	public void queryEntitiesWithFulltextConditionWhereNoStringProperty() {
		Rectangle e = Rectangle.T.create();
		smood.registerEntity(e, false);

		// @formatter:off
		SelectQuery selectQuery = query()
				.from(Rectangle.class, "r").where().fullText("r", MATCH_ALL)
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertResultContains(e);
	}

	@Test
	public void fulltextMatchingAll_Negated() {
		b.person("john").create();

		// @formatter:off
		SelectQuery selectQuery = query()
				.from(Person.T, "p").where().negation().fullText("p", MATCH_ALL)
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertNoMoreResults();
	}

	@Test
	public void matchingAll_1_eq_1() {
		Person p = b.person("john").create();

		// @formatter:off
		SelectQuery selectQuery = query()
				.from(Person.T, "p").where()
					.value(1).eq().value(1)
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertResultContains(p);
	}

	@Test
	public void matchingNothing_1_eq_0() {
		b.person("john").create();

		// @formatter:off
		SelectQuery selectQuery = query()
				.from(Person.T, "p")
				.where()
					.value(1).eq().value(0)
				.orderBy().property("p", "companyName")
				.paging(10, 2)
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertNoMoreResults();
	}

}
