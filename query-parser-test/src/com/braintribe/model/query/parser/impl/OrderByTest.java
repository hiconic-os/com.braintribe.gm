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
import com.braintribe.model.query.OrderingDirection;
import com.braintribe.model.query.Query;
import com.braintribe.model.query.SelectQuery;
import com.braintribe.utils.genericmodel.GMCoreTools;

public class OrderByTest extends AbstractQueryParserTest {

	@Test
	public void testOrderByDefault() throws Exception {

		// @formatter:off
		SelectQuery expectedQuery = sq()
				.from(Person.class, "p")
				.orderBy().property("name")
				.done();
		// @formatter:on

		String queryString = "select * from " + Person.class.getName() + " p order by p.name";

		ParsedQuery parsedQuery = QueryParser.parse(queryString);
		validatedParsedQuery(parsedQuery);
		Query actualQuery = parsedQuery.getQuery();
		GMCoreTools.checkDescription(actualQuery, expectedQuery);
	}

	@Test
	public void testOrderByAsc() throws Exception {

		// @formatter:off
		SelectQuery expectedQuery = sq()
				.from(Person.class, "p")
				.orderBy(OrderingDirection.ascending).property("name")
				.done();
		// @formatter:on

		String queryString = "select * from " + Person.class.getName() + " p order by p.name asc";

		ParsedQuery parsedQuery = QueryParser.parse(queryString);
		validatedParsedQuery(parsedQuery);
		Query actualQuery = parsedQuery.getQuery();
		GMCoreTools.checkDescription(actualQuery, expectedQuery);
	}

	@Test
	public void testOrderByDesc() throws Exception {

		// @formatter:off
		SelectQuery expectedQuery = sq()
				.from(Person.class, "p")
				.orderBy(OrderingDirection.descending).property("name")
				.done();
		// @formatter:on

		String queryString = "select * from " + Person.class.getName() + " p order by p.name desc";

		ParsedQuery parsedQuery = QueryParser.parse(queryString);
		validatedParsedQuery(parsedQuery);
		Query actualQuery = parsedQuery.getQuery();
		GMCoreTools.checkDescription(actualQuery, expectedQuery);
	}

	@Test
	public void testMultipleOrderBy() throws Exception {

		// @formatter:off
		SelectQuery expectedQuery = sq()
				.from(Person.class, "p")
				.orderByCascade()
					.dir(OrderingDirection.descending).property("p", "company.name")
					.dir(OrderingDirection.ascending).value(45) 
					.dir(OrderingDirection.ascending).property("p", "name")
				.close()	
				.done();
		// @formatter:on

		String queryString = "select * from " + Person.class.getName() + " p order by p.company.name desc, 45, p.name asc";

		ParsedQuery parsedQuery = QueryParser.parse(queryString);
		validatedParsedQuery(parsedQuery);
		Query actualQuery = parsedQuery.getQuery();
		GMCoreTools.checkDescription(actualQuery, expectedQuery);
	}

	@Test
	public void orderByAggregateFunction() throws Exception {

		// @formatter:off
		SelectQuery expectedQuery = sq()
				.select("p", "name")
				.select().count("p", "age")
				.from(Person.class, "p")
				.orderBy()
					.count("p", "age")
				.done();
		// @formatter:on

		String queryString = "select p.name, count(p.age) from " + Person.class.getName() + " p order by count(p.age)";

		ParsedQuery parsedQuery = QueryParser.parse(queryString);
		validatedParsedQuery(parsedQuery);
		Query actualQuery = parsedQuery.getQuery();
		GMCoreTools.checkDescription(actualQuery, expectedQuery);
	}

	@Test
	public void orderByAggregateFunction_ExplicitGroupBy() throws Exception {

		// @formatter:off
		SelectQuery expectedQuery = sq()
				.select("p", "name")
				.select().count("p", "age")
				.from(Person.class, "p")
				.groupBy().property("p", "name")
				.orderBy()
					.count("p", "age")
				.done();
		// @formatter:on

		String queryString = "select p.name, count(p.age) from " + Person.class.getName() + " p group by p.name order by count(p.age)";

		ParsedQuery parsedQuery = QueryParser.parse(queryString);
		validatedParsedQuery(parsedQuery);
		Query actualQuery = parsedQuery.getQuery();
		GMCoreTools.checkDescription(actualQuery, expectedQuery);
	}

}
