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

import static com.braintribe.model.generic.GenericEntity.globalId;
import static com.braintribe.model.processing.smood.population.PopulationManager.GLOBAL_ID_INDEX_ID;
import static org.fest.assertions.Assertions.assertThat;

import java.util.Collection;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.braintribe.model.generic.manipulation.Owner;
import com.braintribe.model.generic.value.GlobalEntityReference;
import com.braintribe.model.processing.query.test.builder.PersonBuilder;
import com.braintribe.model.processing.query.test.model.Person;
import com.braintribe.model.processing.smood.test.AbstractSmoodTests;
import com.braintribe.utils.junit.assertions.BtAssertions;

/**
 * 
 */
public class Smood_GlobalId_Test extends AbstractSmoodTests {

	private static final int PERSON_COUNT = 4;
	private Person person;
	private final Person[] persons = new Person[PERSON_COUNT];
	private static final String NAME = "Person Name";

	@Before
	public void buildData() {
		for (int i = 0; i < PERSON_COUNT; i++)
			registerAtSmood(persons[i] = PersonBuilder.newPerson().name("person" + i).globalId("person" + i).create());

		registerAtSmood(person = PersonBuilder.newPerson().name(NAME).globalId(NAME).create());
	}

	@Test
	public void indexIsThere() {
		assertThat(smood.provideIndexInfo(Person.class.getName(), globalId)).isNotNull();
	}

	@Test
	public void findPersonByGlobalId() {
		Person foundPerson = (Person) smood.findEntityByGlobalId(NAME);
		assertSamePerson(foundPerson);
	}

	@Test
	public void findPersonByGlobalId_Index() {
		Person foundPerson = (Person) smood.getValueForIndex(GLOBAL_ID_INDEX_ID, NAME);
		assertSamePerson(foundPerson);
	}

	@Test
	public void findPersonByGlobalId_Refernce() {
		GlobalEntityReference ref = GlobalEntityReference.T.create();
		ref.setRefId(person.getGlobalId());
		ref.setTypeSignature(person.entityType().getTypeSignature());

		Person foundPerson = (Person) smood.findEntity(ref);
		assertSamePerson(foundPerson);
	}

	@Test
	public void findPersonByGlobalId_RefernceForDifferentType() {
		GlobalEntityReference ref = GlobalEntityReference.T.create();
		ref.setRefId(person.getGlobalId());
		ref.setTypeSignature(Owner.T.getTypeSignature());

		Owner foundOwner = smood.findEntity(ref);
		assertThat(foundOwner).isNull();
	}

	@Test
	public void findPersonWhenGlobalIdChanges() {
		person.setGlobalId("other");

		Person foundPerson = (Person) smood.getValueForIndex(GLOBAL_ID_INDEX_ID, "other");
		assertSamePerson(foundPerson);
	}

	private void assertSamePerson(Person foundPerson) {
		assertThat(foundPerson).isNotNull().isSameAs(person);
	}

	@Ignore
	private static void assertContainsPersons(Collection<?> entities, Person p, Person... ps) {
		BtAssertions.assertThat(entities).isNotNull().isNotEmpty().contains(p).contains((Object[]) ps);
	}

}
