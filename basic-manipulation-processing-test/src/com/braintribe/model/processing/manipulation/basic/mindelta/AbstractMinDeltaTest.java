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
package com.braintribe.model.processing.manipulation.basic.mindelta;

import org.junit.Assert;
import org.junit.Before;

import com.braintribe.model.generic.manipulation.AddManipulation;
import com.braintribe.model.generic.manipulation.AtomicManipulation;
import com.braintribe.model.generic.manipulation.ChangeValueManipulation;
import com.braintribe.model.generic.manipulation.RemoveManipulation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.processing.manipulation.AbstractManipulationTest;
import com.braintribe.model.processing.manipulation.testdata.manipulation.TestEntity;

/**
 * @author peter.gazdik
 */
public class AbstractMinDeltaTest extends AbstractManipulationTest {

	protected TestEntity entity;
	private int counter;

	@Before
	public void prepareInstance() {
		entity = session.create(TestEntity.T);
	}

	protected void assertChangeValue(Object expected) {
		ChangeValueManipulation m = getNextManipulation(ChangeValueManipulation.T);
		assertEqual(m.getNewValue(), expected, "Wrong new value.");
	}

	protected void assertRemove(Object expected) {
		RemoveManipulation m = getNextManipulation(RemoveManipulation.T);
		assertEqual(m.getItemsToRemove(), expected, "Wrong remove value.");
	}

	protected void assertAdd(Object expected) {
		AddManipulation am = getNextManipulation(AddManipulation.T);
		assertEqual(am.getItemsToAdd(), expected, "Wrong add value.");
	}

	protected <T extends AtomicManipulation> T getNextManipulation(EntityType<T> entityType) {
		AtomicManipulation am = recordedManipulations.get(counter++);
		if (am.<T> entityType() != entityType) {
			Assert.fail(
					"Manipulation should have type: " + entityType.getShortName() + ", but was: " + am.entityType().getShortName());
		}
		return (T) am;
	}

}
