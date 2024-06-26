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
