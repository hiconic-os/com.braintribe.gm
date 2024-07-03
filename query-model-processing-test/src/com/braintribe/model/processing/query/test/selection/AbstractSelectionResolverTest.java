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
package com.braintribe.model.processing.query.test.selection;

import java.util.List;

import com.braintribe.model.processing.query.api.stringifier.QuerySelection;
import com.braintribe.model.processing.query.selection.BasicQuerySelectionResolver;
import com.braintribe.model.processing.query.test.model.Company;
import com.braintribe.model.processing.query.test.model.Person;
import com.braintribe.model.processing.query.test.stringifier.AbstractSelectQueryTests;
import com.braintribe.model.query.SelectQuery;

public abstract class AbstractSelectionResolverTest extends AbstractSelectQueryTests {
	
	protected List<QuerySelection> stringifyAndResolve(final SelectQuery selectQuery, BasicQuerySelectionResolver resolver) {
		final String queryString = stringify(selectQuery);
		System.out.println(queryString);
		List<QuerySelection> selections = resolver.resolve(selectQuery);
		for (QuerySelection selection : selections) {
			System.out.println(selection.getAlias());
		}
		return selections;
	}
	
	protected SelectQuery singleReferenceQuery() {
		// @formatter:off
		final SelectQuery selectQuery = query()
				.from(Person.class, "_Person")
				.join("_Person", "company", "_Company")
				.select("_Person", "name")
				.select("_Person", "company")
				.select("_Company")
				.select("_Company","name")
				.done();
		// @formatter:on
		return selectQuery;
	}

	protected SelectQuery collectionReferenceQuery() {
		// @formatter:off
		final SelectQuery selectQuery = query()
				.from(Company.class, "_Company")
				.join("_Company", "persons", "_Person")
				.join("_Person", "localizedString", "_LS")
				.select("_Person", "name")
				.select("_Person", "company")
				.select("_Company")
				.select("_Company","name")
				.select("_LS","id")
				.select("_LS")
				.done();
		// @formatter:on
		return selectQuery;
	}

	protected SelectQuery functionQuery() {
		// @formatter:off
		final SelectQuery selectQuery = query()
				.from(Person.class, "_Person")
				.select("_Person")
				.select().count("_Person")
				.select().asString().entity("_Person")
				.select().avg("_Person", "age")
				.select()
					.concatenate()
						.entity("_Person")
						.value(".")
						.property("name")
						.value(":")
						.entity("_Person")
						.value(".")
						.property("companyName")
					.close()
				.done();
		// @formatter:on
		return selectQuery;
	}

	protected SelectQuery wildcardsQuery() {
		// @formatter:off
		final SelectQuery selectQuery = query()
				.from(Person.class, "_Person")
				.from(Company.class, "_Company")
				.join("_Company", "persons", "_CompanyPersons")
				.join("_CompanyPersons", "localizedString", "_LS")
				//.join("_CompanyPersons", "indexedCompany", "_IC")
				.done();
		// @formatter:on
		return selectQuery;
	}



}
