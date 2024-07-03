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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.braintribe.model.processing.query.test.model.Person;
import com.braintribe.model.processing.test.tools.meta.ManipulationTrackingMode;

/**
 * 
 */
public class PersistenceManipulationTests extends AbstractSmoodManipulationTests {

	@Test
	public void deleteDetachedEntityFromSession() {
		applyManipulations(session -> {
			Person p = session.create(Person.T);
			p.setId(99L);
		});

		Person person = smood.findEntity(Person.T, 99L);
		assertThat(person.session()).isNotNull();

		applyManipulations(ManipulationTrackingMode.PERSISTENT, session -> {
			Person p = Person.T.create();
			p.setId(99L);
			p.attach(session);
			session.deleteEntity(p);
		});

		assertThat(person.session()).isNull();

	}

}
