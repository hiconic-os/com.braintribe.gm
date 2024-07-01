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
package com.braintribe.model.processing.query.test.stringifier;

import org.junit.Test;

import com.braintribe.model.processing.query.test.model.Person;
import com.braintribe.model.processing.query.test.model.Rectangle;
import com.braintribe.model.query.SelectQuery;
import com.braintribe.testing.junit.assertions.assertj.core.api.Assertions;

/**
 *
 */
public class ConstantConditionSelectQueryTests extends AbstractSelectQueryTests {
	private static String MATCH_ALL = "";

	/* We had a bug, that the condition was evaluated as true iff there was a String property with non-null value having
	 * given string as substring. But (for now) we want a full-text with empty string to match everything, even if there
	 * is no string property. */
	@Test
	public void queryEntitiesWithFulltextConditionWhereNoStringProperty() {
		// @formatter:off
		 SelectQuery selectQuery = query()
				.from(Rectangle.class, "_Rectangle").where().fullText("_Rectangle", MATCH_ALL)
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery);
		Assertions.assertThat(queryString).isEqualToIgnoringCase(
				"select * from com.braintribe.model.processing.query.test.model.Rectangle _Rectangle where fulltext(_Rectangle, '')");
	}

	@Test
	public void fulltextMatchingAll_Negated() {
		// @formatter:off
		 SelectQuery selectQuery = query()
				.from(Person.class, "_Person").where().negation().fullText("_Person", MATCH_ALL)
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery);
		Assertions.assertThat(queryString).isEqualToIgnoringCase(
				"select * from com.braintribe.model.processing.query.test.model.Person _Person where not fulltext(_Person, '')");
	}

	@Test
	public void matchingAll_1_eq_1() {
		// @formatter:off
		 SelectQuery selectQuery = query()
				.from(Person.class, "_Person").where()
				.value(1).eq().value(1)
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery);
		Assertions.assertThat(queryString)
				.isEqualToIgnoringCase("select * from com.braintribe.model.processing.query.test.model.Person _Person where 1 = 1");
	}

	@Test
	public void matchingNothing_1_eq_0() {
		// @formatter:off
		 SelectQuery selectQuery = query()
				.from(Person.class, "_Person")
				.where()
				.value(1).eq().value(0)
				.orderBy().property("_Person", "companyName")
				.limit(5)
				.paging(2, 10)
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery);
		Assertions.assertThat(queryString).isEqualToIgnoringCase(
				"select * from com.braintribe.model.processing.query.test.model.Person _Person where 1 = 0 order by _Person.companyName asc limit 2 offset 10");
	}

	@Test
	public void queryDisjunctionInConjunction() {
		// @formatter:off
		 SelectQuery selectQuery = query()
				.from(Person.class, "_Person")
				.where()
				.conjunction()
				.disjunction()
				.value(1).eq().value(1)
				.value(3).ne().value(4)
				.close()
				.negation().value(3).eq().value(3)
				.close()
				.limit(5)
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery);
		Assertions.assertThat(queryString).isEqualToIgnoringCase(
				"select * from com.braintribe.model.processing.query.test.model.Person _Person where ((1 = 1 or 3 != 4) and not 3 = 3) limit 5 offset 0");
	}
}
