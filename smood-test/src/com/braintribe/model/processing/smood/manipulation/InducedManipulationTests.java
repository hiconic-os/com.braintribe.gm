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

import java.util.Objects;

import org.junit.Before;
import org.junit.Test;

import com.braintribe.common.lcd.EmptyReadWriteLock;
import com.braintribe.model.generic.value.PreliminaryEntityReference;
import com.braintribe.model.processing.query.test.model.Person;
import com.braintribe.model.processing.session.api.managed.ManipulationMode;
import com.braintribe.model.processing.smood.Smood;
import com.braintribe.model.processing.test.tools.meta.ManipulationTrackingMode;

/**
 * 
 */
public class InducedManipulationTests extends AbstractSmoodManipulationTests {

	private static ManipulationTrackingMode TRACKING_MODE = ManipulationTrackingMode.PERSISTENT;
	private static ManipulationMode MANIPULATION_MODE = TRACKING_MODE.toManipulationMode();

	@Before
	public void setManipulationMode() {
		defaultManipulationMode = TRACKING_MODE;
	}

	Person p;

	/**
	 * In this test we simply track an instantiation manipulation and remember the created entity. Then, we go on to attach this entity to a new Smood
	 * and on that we apply the induced manipulation.
	 * 
	 * NOTE it is important to use the original entity because the induced manipulation uses a {@link PreliminaryEntityReference} that is only valid
	 * for that concrete instance.
	 */
	@Test
	public void checkInducedManipulationIsCorrect() throws Exception {
		applyManipulations(session -> {
			p = session.create(Person.T);
		});

		Objects.isNull(p.getId());
		Objects.isNull(p.getGlobalId());

		Smood smood = new Smood(EmptyReadWriteLock.INSTANCE);
		smood.setMetaModel(provideEnrichedMetaModel());
		smood.registerEntity(p, false);

		smood.apply().generateId(false).manipulationMode(MANIPULATION_MODE).request(asRequest(response.getInducedManipulation()));

		Objects.nonNull(p.getId());
		Objects.nonNull(p.getGlobalId());
	}

	@Test
	public void checkInducedManipulationIsCorrect_WhenPersistenceIdAssignedManuall() throws Exception {
		applyManipulations(session -> {
			p = session.create(Person.T);
			p.setId("p1");
		});

		Objects.nonNull(p.getId());
		Objects.isNull(p.getGlobalId());

		Smood smood = new Smood(EmptyReadWriteLock.INSTANCE);
		smood.setMetaModel(provideEnrichedMetaModel());
		smood.registerEntity(p, false);

		smood.apply().generateId(false).manipulationMode(MANIPULATION_MODE).request(asRequest(response.getInducedManipulation()));

		Objects.nonNull(p.getId());
		Objects.nonNull(p.getGlobalId());
	}
}
