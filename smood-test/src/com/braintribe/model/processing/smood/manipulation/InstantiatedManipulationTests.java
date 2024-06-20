// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
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
