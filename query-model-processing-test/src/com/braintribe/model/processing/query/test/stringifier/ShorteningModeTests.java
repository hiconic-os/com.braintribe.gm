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

import com.braintribe.model.processing.query.shortening.Qualified;
import com.braintribe.model.processing.query.shortening.Simplified;
import com.braintribe.model.processing.query.shortening.SmartShortening;
import com.braintribe.model.processing.query.test.model.Person;
import com.braintribe.model.query.SelectQuery;
import com.braintribe.testing.junit.assertions.assertj.core.api.Assertions;

public class ShorteningModeTests extends AbstractSelectQueryTests {
	@Test
	public void qualifiedTest() {
		// @formatter:off
		 SelectQuery selectQuery = query()
				.select("_Person", "name")
				.from(Person.class, "_Person")
				.distinct()
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery, new Qualified());
		Assertions.assertThat(queryString)
				.isEqualToIgnoringCase("select distinct _Person.name from com.braintribe.model.processing.query.test.model.Person _Person");
	}

	@Test
	public void simplifiedTest() {
		// @formatter:off
		 SelectQuery selectQuery = query()
				.select("_Person", "name")
				.from(Person.class, "_Person")
				.distinct()
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery, new Simplified());
		Assertions.assertThat(queryString).isEqualToIgnoringCase("select distinct _Person.name from Person _Person");
	}

	@Test
	public void smartShorteningTest() {
		// @formatter:off
		SelectQuery selectQuery = query()
			.select("_Person", "name")
			.from(Person.class, "_Person")
			.distinct()
			.done();
		// @formatter:on

		String queryString = stringify(selectQuery, new SmartShortening(getModelOracle()));
		Assertions.assertThat(queryString).isEqualToIgnoringCase("select distinct _Person.name from Person _Person");
	}

	@Test
	public void qualifiedTest2() {
		// @formatter:off
		 SelectQuery selectQuery = query()
				.select("_Person", "name")
				.from("Person", "_Person")
				.distinct()
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery, new Qualified());
		Assertions.assertThat(queryString).isEqualToIgnoringCase("select distinct _Person.name from Person _Person");
	}

	@Test
	public void simplifiedTest2() {
		// @formatter:off
		 SelectQuery selectQuery = query()
				.select("_Person", "name")
				.from("Person", "_Person")
				.distinct()
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery, new Simplified());
		Assertions.assertThat(queryString).isEqualToIgnoringCase("select distinct _Person.name from Person _Person");
	}

	@Test
	public void smartShorteningTest2() {
		// @formatter:off
		 SelectQuery selectQuery = query()
				.select("_Person", "name")
				.from("Person", "_Person")
				.distinct()
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery, new SmartShortening(getModelOracle()));
		Assertions.assertThat(queryString).isEqualToIgnoringCase("select distinct _Person.name from Person _Person");
	}

	@Test
	public void qualifiedTest3() {
		// @formatter:off
		 SelectQuery selectQuery = query()
				.select("_Person", "name")
				.from((String)null, "_Person")
				.distinct()
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery, new Qualified());
		Assertions.assertThat(queryString).isEqualToIgnoringCase("select distinct _Person.name from <?> _Person");
	}

	@Test
	public void simplifiedTest3() {
		// @formatter:off
		 SelectQuery selectQuery = query()
				.select("_Person", "name")
				.from((String)null, "_Person")
				.distinct()
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery, new Simplified());
		Assertions.assertThat(queryString).isEqualToIgnoringCase("select distinct _Person.name from <?> _Person");
	}

	@Test
	public void smartShorteningTest3() {
		// @formatter:off
		 SelectQuery selectQuery = query()
				.select("_Person", "name")
				.from((String)null, "_Person")
				.distinct()
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery, new SmartShortening(getModelOracle()));
		Assertions.assertThat(queryString).isEqualToIgnoringCase("select distinct _Person.name from <?> _Person");
	}
}
