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
package com.braintribe.model.processing.smood;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.braintribe.common.lcd.EmptyReadWriteLock;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.enhance.ManipulationTrackingPropertyAccessInterceptor;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.processing.query.test.builder.PersonBuilder;
import com.braintribe.model.processing.query.test.model.Person;
import com.braintribe.model.processing.session.impl.notifying.BasicNotifyingGmSession;
import com.braintribe.model.processing.session.impl.session.collection.CollectionEnhancingPropertyAccessInterceptor;
import com.braintribe.model.processing.smood.test.AbstractSmoodTests;
import com.braintribe.testing.junit.assertions.assertj.core.api.Assertions;
import com.braintribe.utils.junit.assertions.BtAssertions;

/**
 * 
 */
public class Smood_BasicFunctionality_Test extends AbstractSmoodTests {

	private Person person;
	private static final String NAME = "Some Name";

	@Before
	public void buildData() {
		person = PersonBuilder.newPerson().name(NAME).create();
	}

	@Test
	public void findEntityById() {
		registerAtSmood(person);
		Person foundPerson = smood.findEntity(entityType(person), person.getId());
		assertSamePerson(foundPerson);
	}

	@Test
	public void findEntityByReference() {
		registerAtSmood(person);
		Person foundPerson = smood.findEntity(entityReference(person));
		assertSamePerson(foundPerson);
	}

	@Test
	public void findEntityRegisteredImplicitly() throws Exception {
		smood.initialize(person);
		Person foundPerson = smood.findEntity(entityReference(person));
		assertSamePerson(foundPerson);
	}

	@Test
	public void findEntityCreatedOnSession() throws Exception {
		BasicNotifyingGmSession gmSession = new BasicNotifyingGmSession();
		gmSession.interceptors().add(new CollectionEnhancingPropertyAccessInterceptor());
		gmSession.interceptors().add(new ManipulationTrackingPropertyAccessInterceptor());

		Smood smood2 = new Smood(gmSession, EmptyReadWriteLock.INSTANCE);
		smood2.setMetaModel(smood.getMetaModel());
		smood = smood2;

		person = gmSession.create(Person.T);
		person.setId(10L);

		Person foundPerson = smood.findEntity(entityReference(person));
		assertSamePerson(foundPerson);

		smood.close();
	}

	@Test
	public void doesNotFindUnregisteredEntityById() {
		registerAtSmood(person);
		smood.unregisterEntity(person);

		Person foundPerson = smood.findEntity(entityType(person), person.getId());
		BtAssertions.assertThat(foundPerson).isNull();
	}

	@Test
	public void doesNotFindUnregisteredEntityByIdAmongGenericEntities() {
		registerAtSmood(person);
		smood.unregisterEntity(person);

		Set<GenericEntity> entities = smood.getEntitiesPerType(GenericEntity.T);
		BtAssertions.assertThat(entities).isNullOrEmpty();
	}

	@Test
	public void doesNotFindUnregisteredEntityByReference() {
		registerAtSmood(person);
		smood.unregisterEntity(person);
		Person foundPerson = smood.findEntity(entityReference(person));
		BtAssertions.assertThat(foundPerson).isNull();
	}

	@Test
	public void deletesEntityAndReference() throws Exception {
		Person rh = PersonBuilder.newPerson().name("Ref Holder").friend(person).create();

		registerAtSmood(person);
		registerAtSmood(rh);

		BtAssertions.assertThat(rh.getIndexedFriend()).isNotNull();

		smood.deleteEntity(person);

		BtAssertions.assertThat(rh.getIndexedFriend()).isNull();

		Person foundPerson;
		foundPerson = smood.findEntity(entityReference(rh));
		BtAssertions.assertThat(foundPerson).isNotNull();

		foundPerson = smood.findEntity(entityReference(person));
		BtAssertions.assertThat(foundPerson).isNull();
	}

	@Test
	public void retrievingEntirePopulation() {
		Person person2 = PersonBuilder.newPerson().name(NAME + 2).create();

		registerAtSmood(person, person2);

		BtAssertions.assertThat(smood.getEntitiesPerType(Person.T)).isNotEmpty().containsOnly(person, person2);
		BtAssertions.assertThat(smood.getEntitiesPerType(GenericEntity.T)).isNotEmpty().containsOnly(person, person2);
	}

	@Test
	public void multipleEntitiesWithSameIdNotAllowed() {
		registerAtSmood(person);

		Person person2 = PersonBuilder.newPerson().name(NAME + 2).create();
		registerAtSmood(person2);

		Long id = person.getId();
		Long id2 = person2.getId();

		try {
			person2.setId(id);
			Assertions.fail("Exception should have been thrown.");
			
		} catch (IllegalStateException expected) {
			// noop
		}

		// now check that in Smood everything stayed the same
		EntityType<Person> et = GMF.getTypeReflection().getEntityType(Person.class);
		Person foundPerson = smood.findEntity(et, id);
		Person foundPerson2 = smood.findEntity(et, id2);

		BtAssertions.assertThat(foundPerson).isNotNull().isSameAs(person);
		BtAssertions.assertThat(foundPerson2).isNotNull().isSameAs(person2);
	}

	private void assertSamePerson(Person foundPerson) {
		BtAssertions.assertThat(foundPerson).isNotNull().isSameAs(person);
	}
}
