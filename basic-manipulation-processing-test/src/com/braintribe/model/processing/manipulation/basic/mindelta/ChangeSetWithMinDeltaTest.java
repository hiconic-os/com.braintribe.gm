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

import static com.braintribe.utils.lcd.CollectionTools2.asMap;
import static com.braintribe.utils.lcd.CollectionTools2.asSet;

import java.util.Set;

import org.junit.Test;

import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.processing.manipulation.testdata.manipulation.TestEntity;
import com.braintribe.utils.junit.assertions.BtAssertions;

/**
 * @see ChangeSetWithMinDelta
 */
public class ChangeSetWithMinDeltaTest extends AbstractMinDeltaTest {

	private static Property property = TestEntity.T.getProperty("intSet");

	private Set<Integer> oldSet;
	private Set<Integer> newSet;

	@Test
	public void noChange_Empty() throws Exception {
		oldSet();
		newSet();

		record(session -> ChangeSetWithMinDelta.apply(entity, property, oldSet, newSet));
		BtAssertions.assertThat(recordedManipulations).hasSize(0);
	}

	@Test
	public void noChange_NonEmpty() throws Exception {
		oldSet(1);
		newSet(1);

		record(session -> ChangeSetWithMinDelta.apply(entity, property, oldSet, newSet));
		BtAssertions.assertThat(recordedManipulations).hasSize(0);
	}

	@Test
	public void changeAllValues() throws Exception {
		oldSet(1, 2, 3, 4);
		newSet(5, 6, 7, 8);

		record(session -> ChangeSetWithMinDelta.apply(entity, property, oldSet, newSet));
		assertChangeValue(newSet);
	}

	@Test
	public void onlyRemove() throws Exception {
		oldSet(1, 2, 3, 4);
		newSet(1, 2, 3);

		record(session -> ChangeSetWithMinDelta.apply(entity, property, oldSet, newSet));
		assertRemove(asMap(4, 4));
	}

	@Test
	public void onlyAdd() throws Exception {
		oldSet(1, 2, 3, 4);
		newSet(1, 2, 3, 4, 5);

		record(session -> ChangeSetWithMinDelta.apply(entity, property, oldSet, newSet));
		assertAdd(asMap(5, 5));
	}

	@Test
	public void addMoreThanRemove() throws Exception {
		oldSet(1, 2, 3, 4);
		newSet(1, 2, 3, 5, 6);

		record(session -> ChangeSetWithMinDelta.apply(entity, property, oldSet, newSet));
		assertRemove(asMap(4, 4));
		assertAdd(asMap(5, 5, 6, 6));
	}

	@Test
	public void addFewerThanRemove() throws Exception {
		oldSet(1, 2, 3, 4, 5, 6);
		newSet(1, 2, 3, 4, 7);

		record(session -> ChangeSetWithMinDelta.apply(entity, property, oldSet, newSet));
		assertRemove(asMap(5, 6, 6, 6));
		assertAdd(asMap(7, 7));
	}

	@Test
	public void addFewerThanRemove_CvmIsBetter() throws Exception {
		oldSet(1, 2, 3, 4);
		newSet(1, 2, 5);

		record(session -> ChangeSetWithMinDelta.apply(entity, property, oldSet, newSet));
		assertChangeValue(newSet);
	}

	// #########################################
	// ## . . . . . . . Helpers . . . . . . . ##
	// #########################################

	private void oldSet(Integer... values) {
		entity.setIntSet(asSet(values));
		oldSet = entity.getIntSet();
	}

	private void newSet(Integer... values) {
		newSet = asSet(values);
	}

}
