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

import static com.braintribe.utils.lcd.CollectionTools2.newSet;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import com.braintribe.model.accessapi.ManipulationRequest;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.manipulation.ChangeValueManipulation;
import com.braintribe.model.generic.manipulation.Manipulation;
import com.braintribe.model.generic.reflection.TraversingContext;
import com.braintribe.model.generic.reflection.TraversingVisitor;
import com.braintribe.model.generic.value.PersistentEntityReference;
import com.braintribe.model.processing.query.test.model.Owner;
import com.braintribe.model.processing.query.test.model.Person;
import com.braintribe.model.processing.test.tools.meta.ManipulationTrackingMode;

/**
 * 
 */
public class ManifestUnknowEntitiesManipulationTests extends AbstractSmoodManipulationTests {

	/**
	 * When recording the manipulations, the {@link ChangeValueManipulation} for the name will be referenced using a
	 * {@link PersistentEntityReference}, because we have set an id in the previous step. Thus the process inside the
	 * {@link #asRequest} method will cause our stack to have a non-resolvable reference, which should be manifested.
	 */
	@Test
	public void createEntityForUnknownReference() {
		applyManipulations(ManipulationTrackingMode.PERSISTENT, session -> {
			Person p = session.create(Person.T);
			p.setId(1L);
			p.setName("p2");
		});

		assertEntityCountForType(Owner.T, 0);
		assertEntityCountForType(Person.T, 2);
		assertEntityCountForType(GenericEntity.T, 2);
	}

	@Override
	protected ManipulationRequest asRequest(Manipulation m) {
		ManipulationRequest mr = super.asRequest(m);

		for (PersistentEntityReference ref : findReferences(mr.getManipulation())) {
			ref.setRefId((Long) ref.getRefId() + 100);
		}

		return mr;
	}

	@Override
	protected boolean manifestUnknownEntities() {
		return true;
	}

	private static Set<PersistentEntityReference> findReferences(Manipulation m) {
		return m.touchedEntities() //
				.filter(e -> e instanceof PersistentEntityReference) //
				.map(e -> (PersistentEntityReference) e) //
				.collect(Collectors.toSet());
	}

	static class ReferenceCollectingVisitor implements TraversingVisitor {
		Set<PersistentEntityReference> refs = newSet();

		@Override
		public void visitTraversing(TraversingContext tc) {
			Object o = tc.getObjectStack().peek();

			if (o instanceof PersistentEntityReference)
				refs.add((PersistentEntityReference) o);
		}
	}

}
