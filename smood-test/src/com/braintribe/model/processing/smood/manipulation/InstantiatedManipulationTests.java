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

import static com.braintribe.testing.junit.assertions.gm.assertj.core.api.GmAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.braintribe.model.generic.manipulation.ChangeValueManipulation;
import com.braintribe.model.generic.manipulation.EntityProperty;
import com.braintribe.model.generic.manipulation.Manipulation;
import com.braintribe.model.generic.manipulation.Owner;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.value.EntityReference;
import com.braintribe.model.generic.value.GlobalEntityReference;
import com.braintribe.model.generic.value.PersistentEntityReference;
import com.braintribe.model.processing.query.test.model.Person;
import com.braintribe.model.processing.test.tools.meta.ManipulationTrackingMode;

/**
 * 
 */
public class InstantiatedManipulationTests extends AbstractSmoodManipulationTests {

	@Test
	public void createsEntityWithId() {
		applyManipulations(ManipulationTrackingMode.PERSISTENT, session -> {
			session.createEntity(Person.T).persistent(99L);
		});

		Person person = smood.findEntity(Person.T, 99L);
		assertThat(person.session()).isNotNull();

		assertResponseRef(PersistentEntityReference.T);
	}

	@Test
	public void createsEntityWithGlobalId() {
		applyManipulations(ManipulationTrackingMode.GLOBAL, session -> {
			session.createEntity(Person.T).global("id99");
		});

		Person person = smood.findEntityByGlobalId("id99");
		assertThat(person.session()).isNotNull();

		assertResponseRef(GlobalEntityReference.T);
	}

	private void assertResponseRef(EntityType<? extends EntityReference> refType) {
		Manipulation im = response.getInducedManipulation();
		assertThat(im).isInstanceOf(ChangeValueManipulation.T);

		Owner owner = ((ChangeValueManipulation) im).getOwner();
		assertThat(owner).isInstanceOf(EntityProperty.T);

		EntityReference ref = ((EntityProperty) owner).getReference();
		assertThat(ref).isInstanceOf(refType);
	}
}
