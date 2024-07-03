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
package com.braintribe.model.processing.query.test.stringifier;

import org.junit.Assert;
import org.junit.Test;

import com.braintribe.model.processing.query.test.model.Person;
import com.braintribe.model.query.SelectQuery;

/**
 *
 */
public class QueryFunctionQueryTests extends AbstractSelectQueryTests {

	@Test
	public void asStringCondition() {
		// @formatter:off
		 SelectQuery selectQuery = query()
				.from(Person.class, "_Person")
				.where()
				.asString().property("_Person", "id").like("*")
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery);
		Assert.assertTrue(queryString.equalsIgnoreCase(
				"select * from com.braintribe.model.processing.query.test.model.Person _Person where toString(_Person.id) like '*'"));
	}

	@Test
	public void concatenateCondition() {
		// @formatter:off
		 SelectQuery selectQuery = query()
				.from(Person.class, "_Person")
				.where()
				.concatenate()
				.asString().property("_Person", "id")
				.value(" ")
				.property("_Person", "name")
				.close().like("*")
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery);
		Assert.assertTrue(queryString.equalsIgnoreCase(
				"select * from com.braintribe.model.processing.query.test.model.Person _Person where concatenation(toString(_Person.id), ' ', _Person.name) like '*'"));
	}

	@Test
	public void upperLowerCondition() {
		// @formatter:off
		 SelectQuery selectQuery = query()
				.from(Person.class, "_Person")
				.where()
				.conjunction()
				.asString().upper().property("_Person", "name").eq("JOHN")
				.value("jonny").in().asString().lower().property("_Person", "nicknames")
				.close()
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery);
		Assert.assertTrue(queryString.equalsIgnoreCase(
				"select * from com.braintribe.model.processing.query.test.model.Person _Person where (toString(upper(_Person.name)) = 'JOHN' and 'jonny' in toString(lower(_Person.nicknames)))"));
	}
}
