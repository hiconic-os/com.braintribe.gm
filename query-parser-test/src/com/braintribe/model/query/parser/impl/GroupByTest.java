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
package com.braintribe.model.query.parser.impl;

import org.junit.Test;

import com.braintribe.model.processing.query.parser.QueryParser;
import com.braintribe.model.processing.query.parser.api.ParsedQuery;
import com.braintribe.model.processing.query.test.model.Person;
import com.braintribe.model.query.Query;
import com.braintribe.model.query.SelectQuery;
import com.braintribe.utils.genericmodel.GMCoreTools;

public class GroupByTest extends AbstractQueryParserTest {

	@Test
	public void testGroupByNonSelected() throws Exception {

		// @formatter:off
		SelectQuery expectedQuery = sq()
				.from(Person.class, "p")
				.select().count("p", "age")
				.groupBy("p", "name")
				.done();
		// @formatter:on

		String queryString = "select count(p.age) from " + Person.class.getName() + " p group by p.name";

		ParsedQuery parsedQuery = QueryParser.parse(queryString);
		validatedParsedQuery(parsedQuery);
		Query actualQuery = parsedQuery.getQuery();
		GMCoreTools.checkDescription(actualQuery, expectedQuery);
	}

	@Test
	public void testGroupBySelected() throws Exception {

		// @formatter:off
		SelectQuery expectedQuery = sq()
				.from(Person.class, "p")
				.select().count("p", "age")
				.select("p", "name")
				.groupBy("p", "name")
				.done();
		// @formatter:on

		String queryString = "select count(p.age),p.name from " + Person.class.getName() + " p group by p.name";

		ParsedQuery parsedQuery = QueryParser.parse(queryString);
		validatedParsedQuery(parsedQuery);
		Query actualQuery = parsedQuery.getQuery();
		GMCoreTools.checkDescription(actualQuery, expectedQuery);
	}

	@Test
	public void testGroupByMultiple() throws Exception {

		// @formatter:off
		SelectQuery expectedQuery = sq()
				.from(Person.class, "p")
				.select().count("p", "age")
				.select().entitySignature().entity("p")
				.select("p", "name")
				.groupBy("p", "name").groupBy().entitySignature().entity("p")
				.done();
		// @formatter:on

		String queryString = "select count(p.age), typeSignature(p), p.name from " + Person.class.getName() + " p group by p.name, typeSignature(p)";

		ParsedQuery parsedQuery = QueryParser.parse(queryString);
		validatedParsedQuery(parsedQuery);
		Query actualQuery = parsedQuery.getQuery();
		GMCoreTools.checkDescription(actualQuery, expectedQuery);
	}

	@Test
	public void withHaving() throws Exception {

		// @formatter:off
		SelectQuery expectedQuery = sq()
				.from(Person.class, "p")
				.select().count("p", "age")
				.select("p", "name")
				.groupBy("p", "name")
				.having().count("p", "age").ge(5)
				.done();
		// @formatter:on

		String queryString = "select count(p.age),p.name from " + Person.class.getName() + " p group by p.name having count(p.age) >= 5";

		ParsedQuery parsedQuery = QueryParser.parse(queryString);
		validatedParsedQuery(parsedQuery);
		Query actualQuery = parsedQuery.getQuery();
		GMCoreTools.checkDescription(actualQuery, expectedQuery);
	}

}
