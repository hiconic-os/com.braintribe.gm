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
import com.braintribe.model.processing.query.test.model.Owner;
import com.braintribe.model.query.Query;
import com.braintribe.model.query.SelectQuery;
import com.braintribe.utils.genericmodel.GMCoreTools;

public class JoinFunctionTest extends AbstractQueryParserTest {

	@Test
	public void testListIndexSelection() throws Exception {
		// @formatter:off
		SelectQuery expectedQuery = sq()
				.from(Owner.class, "o")
				.select("cs", "name")
				.select().listIndex("cs")
				.join("o", "companyList", "cs")
				.done();
		// @formatter:on		

		String queryString = "select cs.name,listIndex(cs) from " + Owner.class.getName() + " o join o.companyList cs ";

		ParsedQuery parsedQuery = QueryParser.parse(queryString);
		validatedParsedQuery(parsedQuery);
		Query actualQuery = parsedQuery.getQuery();
		GMCoreTools.checkDescription(actualQuery, expectedQuery);
	}

	@Test
	public void testListIndexCondition() throws Exception {
		// @formatter:off
		SelectQuery expectedQuery = sq()
				.from(Owner.class, "o")
				.select("cs", "name")
				.join("o", "companyList", "cs")
				.where()
					.listIndex("cs").le(1)
				.done();
		// @formatter:on		

		String queryString = "select cs.name from " + Owner.class.getName() + " o join o.companyList cs where listIndex(cs) <= 1";

		ParsedQuery parsedQuery = QueryParser.parse(queryString);
		validatedParsedQuery(parsedQuery);
		Query actualQuery = parsedQuery.getQuery();
		GMCoreTools.checkDescription(actualQuery, expectedQuery);
	}

	@Test
	public void testMapKeySelection() throws Exception {
		// @formatter:off
		SelectQuery expectedQuery = sq()
				.from(Owner.class, "o")
				.select("cs", "name")
				.select().mapKey("cs")
				.join("o", "companyMap", "cs")
				.done();
		// @formatter:on		

		String queryString = "select cs.name, mapKey(cs) from " + Owner.class.getName() + " o join o.companyMap cs ";

		ParsedQuery parsedQuery = QueryParser.parse(queryString);
		validatedParsedQuery(parsedQuery);
		Query actualQuery = parsedQuery.getQuery();
		GMCoreTools.checkDescription(actualQuery, expectedQuery);
	}

	@Test
	public void testMapKeyCondition() throws Exception {
		// @formatter:off
		SelectQuery expectedQuery = sq()
				.from(Owner.class, "o")
				.select("cs", "name")
				.join("o", "companyMap", "cs")
				.where()
					.mapKey("cs").ne(1)
				.done();
		// @formatter:on		

		String queryString = "select cs.name from " + Owner.class.getName() + " o join o.companyMap cs where mapKey(cs) != 1";

		ParsedQuery parsedQuery = QueryParser.parse(queryString);
		validatedParsedQuery(parsedQuery);
		Query actualQuery = parsedQuery.getQuery();
		GMCoreTools.checkDescription(actualQuery, expectedQuery);
	}
}
