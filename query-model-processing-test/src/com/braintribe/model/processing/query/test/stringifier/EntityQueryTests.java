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

import static com.braintribe.testing.junit.assertions.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.braintribe.model.processing.query.fluent.EntityQueryBuilder;
import com.braintribe.model.processing.query.test.model.Person;
import com.braintribe.model.query.EntityQuery;

public class EntityQueryTests extends AbstractSelectQueryTests {

	@Test
	public void queryEntities() {
		EntityQuery query = EntityQueryBuilder.from(Person.class).done();
		String queryString = stringify(query);

		assertThat(queryString).isEqualToIgnoringCase("from com.braintribe.model.processing.query.test.model.Person");
	}

	@Test
	public void queryEntitiesWithCondition() {
		EntityQuery query = EntityQueryBuilder.from(Person.class).where().property("name").eq("Jack").done();
		String queryString = stringify(query);

		assertThat(queryString).isEqualToIgnoringCase("from com.braintribe.model.processing.query.test.model.Person where name = 'Jack'");
	}

	@Test
	public void queryEntitiesWithPaging() {
		EntityQuery query = EntityQueryBuilder.from(Person.class).where().property("name").eq("Jack").paging(2, 5).done();
		String queryString = stringify(query);

		assertThat(queryString)
				.isEqualToIgnoringCase("from com.braintribe.model.processing.query.test.model.Person where name = 'Jack' limit 2 offset 5");
	}

	@Test
	public void queryEntitiesWithFulltext() {
		EntityQuery query = EntityQueryBuilder.from(Person.class).where().fullText(null,"foo").done();
		String queryString = stringify(query);

		assertThat(queryString).isEqualToIgnoringCase("from com.braintribe.model.processing.query.test.model.Person where fullText( 'foo')");
	}

}
