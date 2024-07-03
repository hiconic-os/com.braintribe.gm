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
package com.braintribe.model.processing.smood.querying;

import java.util.List;

import org.junit.Test;

import com.braintribe.model.access.ModelAccessException;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.processing.query.fluent.EntityQueryBuilder;
import com.braintribe.model.processing.query.test.model.Person;
import com.braintribe.model.processing.smood.test.AbstractSmoodTests;
import com.braintribe.model.query.EntityQuery;
import com.braintribe.utils.junit.assertions.BtAssertions;

/**
 * 
 */
public class FulltextQueryTests extends AbstractSmoodTests {

	private List<GenericEntity> entities;

	@Test
	public void queryEntitiesWithFulltext() throws Exception {
		Person p1 = b.person("Jack").create();
		Person p2 = b.person("John").create();

		EntityQuery query = EntityQueryBuilder.from(Person.T).where().fullText(null, "J").done();
		queryEntities(query);

		BtAssertions.assertThat(entities).containsOnly(p1, p2);
	}

	@Test
	public void queryEntitiesWithFulltextOnLS() throws Exception {
		Person p1 = b.person("Jack").localizedString("default", "First_LS").create();
		Person p2 = b.person("John").localizedString("default", "Second_LS").create();

		EntityQuery query = EntityQueryBuilder.from(Person.T).where().fullText(null, "_LS").done();
		queryEntities(query);

		BtAssertions.assertThat(entities).containsOnly(p1, p2);
	}

	private void queryEntities(EntityQuery query) throws ModelAccessException {
		entities = smood.queryEntities(query).getEntities();
	}

}
