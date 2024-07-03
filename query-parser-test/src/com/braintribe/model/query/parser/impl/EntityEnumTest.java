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

import com.braintribe.model.generic.value.EnumReference;
import com.braintribe.model.generic.value.PersistentEntityReference;
import com.braintribe.model.generic.value.PreliminaryEntityReference;
import com.braintribe.model.processing.query.parser.QueryParser;
import com.braintribe.model.processing.query.parser.api.ParsedQuery;
import com.braintribe.model.processing.query.test.model.Person;
import com.braintribe.model.query.Query;
import com.braintribe.model.query.SelectQuery;
import com.braintribe.model.query.conditions.ValueComparison;
import com.braintribe.testing.model.test.technical.features.SimpleEnum;
import com.braintribe.utils.genericmodel.GMCoreTools;

public class EntityEnumTest extends AbstractQueryParserTest {

	@Test
	public void testPreliminaryEntityReference() throws Exception {
		PreliminaryEntityReference reference = PreliminaryEntityReference.T.create();
		reference.setTypeSignature(Person.class.getName());
		reference.setRefId(23);

		// @formatter:off
		SelectQuery expectedQuery = sq()
				.from(Person.class, "p")
				.where()
					.entity(reference).ne().value(null)
				.done();
		// @formatter:on

		((ValueComparison) expectedQuery.getRestriction().getCondition()).setLeftOperand(reference);

		String queryString = "select * from " + Person.class.getName() + " p where reference(" + Person.class.getName() + ",23,false) != null";

		ParsedQuery parsedQuery = QueryParser.parse(queryString);
		validatedParsedQuery(parsedQuery);
		Query actualQuery = parsedQuery.getQuery();
		GMCoreTools.checkDescription(actualQuery, expectedQuery);
	}

	@Test
	public void testPersistentEntityReference() throws Exception {
		PersistentEntityReference reference = PersistentEntityReference.T.create();
		reference.setTypeSignature(Person.class.getName());
		reference.setRefId(23);
		// @formatter:off
		SelectQuery expectedQuery = sq()
				.from(Person.class, "p")
				.where()
					.entity(reference).ne().value(null)
				.done();
		// @formatter:on

		((ValueComparison) expectedQuery.getRestriction().getCondition()).setLeftOperand(reference);

		String queryString = "select * from " + Person.class.getName() + " p where reference(" + Person.class.getName() + ",23,true) != null";

		ParsedQuery parsedQuery = QueryParser.parse(queryString);
		validatedParsedQuery(parsedQuery);
		Query actualQuery = parsedQuery.getQuery();
		GMCoreTools.checkDescription(actualQuery, expectedQuery);
	}

	@Test
	public void testPersistentEntityReferenceWithPartition() throws Exception {
		PersistentEntityReference reference = PersistentEntityReference.T.create();
		reference.setTypeSignature(Person.class.getName());
		reference.setRefId(23);
		reference.setRefPartition("cortex");

		// @formatter:off
		SelectQuery expectedQuery = sq()
				.from(Person.T, "p")
				.where()
					.entity(reference).ne().value(null)
				.done();
		// @formatter:on

		((ValueComparison) expectedQuery.getRestriction().getCondition()).setLeftOperand(reference);

		String queryString = "select * from " + Person.class.getName() + " p where reference(" + Person.class.getName()
				+ ",23,'cortex',true) != null";

		ParsedQuery parsedQuery = QueryParser.parse(queryString);
		validatedParsedQuery(parsedQuery);
		Query actualQuery = parsedQuery.getQuery();
		GMCoreTools.checkDescription(actualQuery, expectedQuery);
	}

	@Test
	public void testEnumReference() throws Exception {
		EnumReference reference = EnumReference.T.create();
		reference.setTypeSignature(SimpleEnum.class.getName());
		reference.setConstant("TWO");

		// @formatter:off
		SelectQuery expectedQuery = sq()
				.from(Person.T, "p")
				.where()
					.entity(reference).ne().value(null)
				.done();
		// @formatter:on

		((ValueComparison) expectedQuery.getRestriction().getCondition()).setLeftOperand(reference);

		String queryString = "select * from " + Person.class.getName() + " p where enum(" + SimpleEnum.class.getName() + ", TWO) != null";

		ParsedQuery parsedQuery = QueryParser.parse(queryString);
		validatedParsedQuery(parsedQuery);
		Query actualQuery = parsedQuery.getQuery();
		GMCoreTools.checkDescription(actualQuery, expectedQuery);
	}
}
