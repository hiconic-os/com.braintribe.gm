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

import static com.braintribe.utils.lcd.CollectionTools2.asSet;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.processing.query.fluent.SelectQueryBuilder;
import com.braintribe.model.processing.query.test.builder.PersonBuilder;
import com.braintribe.model.processing.query.test.model.Person;
import com.braintribe.model.processing.smood.test.AbstractSmoodTests;
import com.braintribe.utils.junit.assertions.BtAssertions;

/**
 * 
 */
public class Smood_MultiplePartitions_Test extends AbstractSmoodTests {

	private Person person1;
	private Person person2;

	private static final String NAME1 = "Some Name 1";
	private static final String NAME2 = "Some Name 2";

	private static final String PARTITION1 = "Partition 1";
	private static final String PARTITION2 = "Partition 2";

	@Test
	public void canInitializeWithSameIdDifferentPartitions() throws Exception {
		person1 = PersonBuilder.newPerson().name(NAME1).id(1L).partition(PARTITION1).create();
		person2 = PersonBuilder.newPerson().name(NAME2).id(1L).partition(PARTITION2).create();

		smood.setPartitions(asSet(PARTITION1, PARTITION2));
		smood.initialize(Arrays.asList(person1, person2));

		// We need to test that when looking by id, we get both instances
		List<Object> results = smood.query(query().from(Person.class, "p").where().property("p", "id").eq(1L).done()).getResults();

		BtAssertions.assertThat(results).hasSize(2);
		BtAssertions.assertThat(results).containsOnly(person1, person2);
	}

	@Test
	public void resolveReferences() throws Exception {
		person1 = PersonBuilder.newPerson().name(NAME1).id(1L).partition(PARTITION1).create();
		person2 = PersonBuilder.newPerson().name(NAME2).id(1L).partition(PARTITION2).create();

		smood.setPartitions(asSet(PARTITION1, PARTITION2));
		smood.initialize(Arrays.asList(person1, person2));

		GenericEntity p1 = smood.findEntity(person1.reference());
		GenericEntity p2 = smood.findEntity(person2.reference());

		BtAssertions.assertThat(p1).isSameAs(person1);
		BtAssertions.assertThat(p2).isSameAs(person2);
	}

	// Actually, this is not longer the case, we are OK with having same ids for different, even if related, types.
	// @Test
	// public void typeAndSubType() throws Exception {
	// person1 = PersonBuilder.newPerson().name(NAME1).partition(PARTITION1).create();
	// person2 = OwnerBuilder.newOwner().name(NAME2).partition(PARTITION2).create();
	//
	// smood.registerEntity(person1, true);
	// smood.registerEntity(person2, true);
	//
	// BtAssertions.assertThat(person1.<Object> getId()).isNotEqualTo(person2.getId());
	// }

	private static SelectQueryBuilder query() {
		return new SelectQueryBuilder();
	}

}
