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

import org.junit.Before;
import org.junit.Test;

import com.braintribe.model.accessapi.ManipulationRequest;
import com.braintribe.model.generic.manipulation.CompoundManipulation;
import com.braintribe.model.generic.manipulation.Manipulation;
import com.braintribe.model.processing.query.test.model.Person;
import com.braintribe.model.processing.session.api.notifying.NotifyingGmSession;

/**
 * 
 */
public class GlobalManipulationTests extends AbstractSmoodManipulationTests {

	private boolean adjust;

	@Before
	public void setManipulationMode() {
		defaultManipulationMode = GLOBAL;

		adjust = false;
		applyManipulations(this::createPerson);
		adjust = true;
	}

	@Test
	public void createEntityAndSetId() {
		applyManipulations(session -> {
			Person p = createPerson(session);
			p.setName("p1");
		});

	}

	private Person createPerson(NotifyingGmSession session) {
		Person p = session.create(Person.T);
		p.setGlobalId("person1");

		return p;
	}

	@Override
	protected ManipulationRequest asRequest(Manipulation m) {
		if (adjust) {
			CompoundManipulation cm = (CompoundManipulation) m;
			cm.getCompoundManipulationList().remove(0); // instantiation
			cm.getCompoundManipulationList().remove(0); // set globalId to "p1"
		}

		return super.asRequest(m);
	}

}
