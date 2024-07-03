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
package com.braintribe.model.processing.smood.manipulation;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.processing.query.test.model.Owner;
import com.braintribe.model.processing.query.test.model.Person;
import com.braintribe.model.processing.test.tools.meta.ManipulationTrackingMode;
import com.braintribe.utils.junit.assertions.BtAssertions;

/**
 * 
 */
public class LocalManipulationTests extends AbstractSmoodManipulationTests {

	@Before
	public void setManipulationMode() {
		defaultManipulationMode = ManipulationTrackingMode.LOCAL;
	}

	@Test
	public void createEntityAndSetId() {
		applyManipulations(ManipulationTrackingMode.LOCAL, session -> {
			Person p = session.create(Person.T);
			p.setId(1L);
		});

		assertEntityCountForType(Owner.T, 0);
		assertEntityCountForType(Person.T, 1);
		assertEntityCountForType(GenericEntity.T, 1);
	}

	@Test
	public void createEntityAndSetProperty() {
		applyManipulations(session -> {
			Person entity = session.create(Person.T);
			entity.setName("My Name");
		});

		assertEntityCountForType(Person.T, 1);
		assertFindsByProperty(Person.T, "name", "My Name");
	}

	@Test
	public void createEntityAndSetIndexedProperty() {
		applyManipulations(session -> {
			Person entity = session.create(Person.T);
			entity.setIndexedName("My Name");
		});

		assertEntityCountForType(Person.T, 1);
		assertFindsByIndexedProperty(Person.T, "indexedName", "My Name");
	}

	@Test
	public void createEntity_AndGetByIdAndReference() throws Exception {
		applyManipulations(session -> {
			session.create(Person.T);
		});

		Person person1 = smood.getEntitiesPerType(Person.T).iterator().next();

		Object person2 = smood.getEntity(Person.T, person1.getId());
		BtAssertions.assertThat(person2).isEqualTo(person1);

		Object person3 = smood.getEntity(person1.reference());
		BtAssertions.assertThat(person3).isEqualTo(person1);
	}

	@Test
	public void createEntity_ChangeId_AndGetByIdAndReference() throws Exception {
		applyManipulations(session -> {
			session.create(Person.T);
		});

		Person person1 = smood.getEntitiesPerType(Person.T).iterator().next();
		person1.setId(150L);

		Object person2 = smood.getEntity(Person.T, person1.getId());
		BtAssertions.assertThat(person2).isEqualTo(person1);

		Object person3 = smood.getEntity(person1.reference());
		BtAssertions.assertThat(person3).isEqualTo(person1);
	}

	@Test
	public void createEntity_ChangeIdToNull_AndGetByIdAndReference() throws Exception {
		applyManipulations(session -> {
			session.create(Person.T);
		});

		Person person1 = smood.getEntitiesPerType(Person.T).iterator().next();
		person1.setId(null);

		Set<Person> allEntities = smood.getEntitiesPerType(Person.T);
		BtAssertions.assertThat(allEntities).isNotEmpty().contains(person1);

		Object person3 = smood.getEntity(person1.reference());
		BtAssertions.assertThat(person3).isEqualTo(person1);
	}

}
