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
public class QueryFunctionQueryTests extends AbstractSelectQueryTests {

	/** SeeQueryFunctionTests#asStringCondition() */
	@Test
	public void asStringCondition() {
		Person p1 = b.person("Jack").create();
		Person p2 = b.person("John").create();

		// @formatter:off
		SelectQuery selectQuery = query()
				.from(Person.T, "person")
				.where()
					.asString().property("person", "id").like("*")
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertResultContains(p1);
		assertResultContains(p2);
		assertNoMoreResults();
	}

	@Test
	public void ilikeWithEscapeCharacter() {
		Person p;
		p = b.person("Jack").create();
		p = b.person("John\\John").create();

		// @formatter:off
		SelectQuery selectQuery = query()
				.from(Person.T, "p")
				.where()
					.property("p", "name").ilike("john\\\\john")
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertResultContains(p);
		assertNoMoreResults();
	}
}
