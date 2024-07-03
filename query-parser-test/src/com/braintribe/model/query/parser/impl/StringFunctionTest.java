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

public class StringFunctionTest extends AbstractQueryParserTest {

	@Test
	public void testLower() throws Exception {

		// @formatter:off
		SelectQuery expectedQuery = sq()
				.from(Person.class, "p")
				.where()
					.asString().lower().property("p", "name").eq().value("n")
				.done();
		// @formatter:on

		String queryString = "select * from " + Person.class.getName() + " p where toString(lower(p.name)) = 'n'";

		ParsedQuery parsedQuery = QueryParser.parse(queryString);
		validatedParsedQuery(parsedQuery);
		Query actualQuery = parsedQuery.getQuery();
		GMCoreTools.checkDescription(actualQuery, expectedQuery);
	}

	@Test
	public void testUpper() throws Exception {

		// @formatter:off
		SelectQuery expectedQuery = sq()
				.from(Person.class, "p")
				.where()
					.asString().upper().property("p", "name").eq().value("n")
				.done();
		// @formatter:on

		String queryString = "select * from " + Person.class.getName() + " p where toString(upper(p.name)) = 'n'";

		ParsedQuery parsedQuery = QueryParser.parse(queryString);
		validatedParsedQuery(parsedQuery);
		Query actualQuery = parsedQuery.getQuery();
		GMCoreTools.checkDescription(actualQuery, expectedQuery);
	}

	@Test
	public void testConcatenate() throws Exception {

		// @formatter:off
		SelectQuery expectedQuery = sq()
				.from(Person.class, "p")
				.where()
					.concatenate()
						.lower().property("p", "name")
						.upper().property("p","indexedName")
						.value("q")
					.close()
					.eq().value("n")
				.done();
		// @formatter:on

		String queryString = "select * from " + Person.class.getName() + " p where concatenation(lower(p.name),upper(p.indexedName),'q') = 'n'";

		ParsedQuery parsedQuery = QueryParser.parse(queryString);
		validatedParsedQuery(parsedQuery);
		Query actualQuery = parsedQuery.getQuery();
		GMCoreTools.checkDescription(actualQuery, expectedQuery);
	}

	@Test
	public void testNestedStringFunctions() throws Exception {

		// @formatter:off
		SelectQuery expectedQuery = sq()
				.from(Person.class, "p")
				.where()
					.asString().lower().upper().property("p", "name").eq().value("n")
				.done();
		// @formatter:on

		String queryString = "select * from " + Person.class.getName() + " p where toString(lower(upper(p.name))) = 'n'";

		ParsedQuery parsedQuery = QueryParser.parse(queryString);
		validatedParsedQuery(parsedQuery);
		Query actualQuery = parsedQuery.getQuery();
		GMCoreTools.checkDescription(actualQuery, expectedQuery);
	}

	@Test
	public void testLocalize() throws Exception {

		// @formatter:off
		SelectQuery expectedQuery = sq()
				.from(Person.class, "p")
				.select("p")
				.select().localize("pt").property("p", "localizedString")
				.done();
		// @formatter:on

		String queryString = "select p, localize(p.localizedString,'pt') from " + Person.class.getName() + " p";

		ParsedQuery parsedQuery = QueryParser.parse(queryString);
		validatedParsedQuery(parsedQuery);
		Query actualQuery = parsedQuery.getQuery();
		GMCoreTools.checkDescription(actualQuery, expectedQuery);
	}
}
