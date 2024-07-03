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

import org.junit.Test;

import com.braintribe.model.processing.query.test.model.Owner;
import com.braintribe.model.processing.query.test.model.Person;
import com.braintribe.model.query.OrderingDirection;
import com.braintribe.model.query.SelectQuery;
import com.braintribe.testing.junit.assertions.assertj.core.api.Assertions;

/**
 *
 */
public class DistinctSelectionQueryTests extends AbstractSelectQueryTests {

	@Test
	public void simpleProperty() {
		// @formatter:off
		 SelectQuery selectQuery = query()
				.select("_Person", "name")
				.from(Person.class, "_Person")
				.distinct()
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery);
		Assertions.assertThat(queryString)
				.isEqualToIgnoringCase("select distinct _Person.name from com.braintribe.model.processing.query.test.model.Person _Person");
	}

	@Test
	public void simpleProperty_OrderedByOtherProperty() {
		// @formatter:off
		 SelectQuery selectQuery = query()
				.select("_Person", "name")
				.from(Person.class, "_Person")
				.distinct()
				.orderBy(OrderingDirection.descending).property("_Person", "age")
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery);
		Assertions.assertThat(queryString).isEqualToIgnoringCase(
				"select distinct _Person.name from com.braintribe.model.processing.query.test.model.Person _Person order by _Person.age desc");
	}

	@Test
	public void listProperty() {
		// @formatter:off
		 SelectQuery selectQuery = query()
				.select("_Owner", "companyList")
				.from(Owner.class, "_Owner")
				.distinct()
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery);
		Assertions.assertThat(queryString)
				.isEqualToIgnoringCase("select distinct _Owner.companyList from com.braintribe.model.processing.query.test.model.Owner _Owner");
	}

	@Test
	public void setProperty() {
		// @formatter:off
		 SelectQuery selectQuery = query()
				.select("_Person", "nicknames")
				.from(Person.class, "_Person")
				.distinct()
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery);
		Assertions.assertThat(queryString)
				.isEqualToIgnoringCase("select distinct _Person.nicknames from com.braintribe.model.processing.query.test.model.Person _Person");
	}
}
