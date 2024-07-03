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

import static com.braintribe.model.processing.test.tools.meta.ManipulationTrackingMode.GLOBAL;
import static com.braintribe.model.processing.test.tools.meta.ManipulationTrackingMode.LOCAL;
import static com.braintribe.model.processing.test.tools.meta.ManipulationTrackingMode.PERSISTENT;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.processing.query.test.model.Company;
import com.braintribe.model.processing.query.test.model.Owner;
import com.braintribe.model.processing.query.test.model.Person;
import com.braintribe.model.processing.session.api.managed.ManipulationMode;
import com.braintribe.model.processing.test.tools.meta.ManipulationTrackingMode;

/**
 * These are the tests for use-cases which make sense for more than one {@link ManipulationMode}.
 * 
 * @see LocalManipulationTests
 */
@RunWith(Parameterized.class)
public class BasicManipulationTests extends AbstractSmoodManipulationTests {

	@Parameters(name = "${0}")
	public static Collection<Object> params() {
		return Arrays.asList(new Object[] { LOCAL, PERSISTENT, GLOBAL });
	}

	public BasicManipulationTests(ManipulationTrackingMode mode) {
		this.defaultManipulationMode = mode;
	}

	@Test
	public void createEntity() {
		applyManipulations(session -> {
			session.create(Person.T);
		});

		assertEntityCountForType(Owner.T, 0);
		assertEntityCountForType(Person.T, 1);
		assertEntityCountForType(GenericEntity.T, 1);
	}

	@Test
	public void createAndDeleteEntity() {
		applyManipulations(session -> {
			Person p = session.create(Person.T);
			session.deleteEntity(p);
		});

		assertEntityCountForType(Owner.T, 0);
		assertEntityCountForType(Person.T, 0);
		assertEntityCountForType(GenericEntity.T, 0);
	}

	@Test
	public void createAndDeleteEntity_WhileSettingId() {
		applyManipulations(session -> {
			Person p = session.create(Person.T);
			p.setId(1L);
			session.deleteEntity(p);
		});

		assertEntityCountForType(Owner.T, 0);
		assertEntityCountForType(Person.T, 0);
		assertEntityCountForType(GenericEntity.T, 0);
	}

	/**
	 * This was causing a problem since there are two instances with same id. This was fixed by saying that sometimes an id is not unique.
	 */
	@Test
	public void createTwoEntitiesWithSameIdAndDeleteOneOfThem() {
		applyManipulations(session -> {
			Person p = session.create(Person.T);
			p.setId(1l);
			Company c = session.create(Company.T);
			c.setId(1l);

			session.deleteEntity(p);
		});

		assertEntityCountForType(Owner.T, 0);
		assertEntityCountForType(Person.T, 0);
		assertEntityCountForType(Company.T, 1);
		assertEntityCountForType(GenericEntity.T, 1);
	}

}
