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
package com.braintribe.model.processing.manipulation.basic.normalization;

import static com.braintribe.model.generic.manipulation.util.ManipulationBuilder.absenting;
import static com.braintribe.model.generic.manipulation.util.ManipulationBuilder.manifestation;
import static com.braintribe.utils.lcd.CollectionTools2.newList;

import java.util.HashSet;

import org.fest.assertions.Assertions;
import org.junit.Test;

import com.braintribe.model.generic.manipulation.AbsentingManipulation;
import com.braintribe.model.generic.manipulation.ChangeValueManipulation;
import com.braintribe.model.generic.manipulation.EntityProperty;
import com.braintribe.model.processing.manipulation.testdata.manipulation.TestEntity;

/**
 * 
 */
public class SimpleNormalizationTests extends AbstractSimpleNormalizerTests {

	@Test
	public void instantiationAndUninstantiation() {
		record(session -> {
			session.create(TestEntity.T);
		});

		recordedManipulations.add(inverse(getRecordedManipulation(0)));

		normalize();
		assertEmpty();
	}

	@Test
	public void instantiationSettingSimpleValuesAndUninstantiation() {
		record(session -> {
			TestEntity entity = session.create(TestEntity.T);
			entity.setProperty1("value1");
			entity.setProperty2("value2");
		});

		recordedManipulations.add(inverse(getRecordedManipulation(0)));

		normalize();
		assertEmpty();
	}

	@Test
	public void instantiationSettingCollectionValuesAndUninstantiation() {
		record(session -> {
			TestEntity entity = session.create(TestEntity.T);
			entity.setSomeSet(new HashSet<TestEntity>());
			entity.getSomeSet().add(entity);
		});

		recordedManipulations.add(inverse(getRecordedManipulation(0)));

		normalize();
		assertEmpty();
	}

	@Test
	public void settingPropsAndAsPropAndThenDeletingPersistentEntity() {
		final TestEntity entity = createEntity("test", 45);

		record(session -> {
			TestEntity tmpEntity = session.create(TestEntity.T); // will be removed from the stack
			entity.setParentEntity(tmpEntity); // will be normalized to entity.setParentEntity(null);
			tmpEntity.setParentEntity(entity); // will be removed from the stack
		});

		recordedManipulations.add(inverse(getRecordedManipulation(0)));

		normalize();
		assertManiCount(1);
		assertPositions(-1, 0, -1, -1);

		// check that second manipulation is change to entity.setParentEntity(null);
		ChangeValueManipulation cvm = (ChangeValueManipulation) normalizedManipulations.get(0);
		Assertions.assertThat(cvm.getNewValue()).as("New value for 'entity.parentEntity' should be null!").isNull();
	}

	@Test
	public void insertingIntoCollectionAndChangingCollectionValue() {
		final TestEntity entity = session.create(TestEntity.T);

		record(session -> {
			entity.setSomeSet(new HashSet<TestEntity>());
			entity.getSomeSet().add(entity);
			entity.setSomeSet(new HashSet<TestEntity>());
		});

		normalize();
		assertManiCount(1);
		assertPositions(-1, -1, 0);
	}

	@Test
	public void insertingIntoCollectionAndClearingCollection() {
		final TestEntity entity = session.create(TestEntity.T);

		record(session -> {
			entity.setSomeList(newList());
			entity.getSomeList().add(entity);
			entity.getSomeList().add(entity);
			entity.getSomeList().clear();
		});

		normalize();
		assertManiCount(1);
		assertPositions(-1, -1, -1, 0);
	}

	@Test
	public void singleSettingOfProperty() {
		final TestEntity entity = session.create(TestEntity.T);

		record(session -> {
			entity.setProperty1("1");
			entity.setProperty2("2");
		});

		normalize();
		assertManiCount(2);
	}

	@Test
	public void multipleSettingOfSameProperty() {
		final TestEntity entity = session.create(TestEntity.T);

		record(session -> {
			entity.setProperty1("x");
			entity.setProperty2("");
			entity.setProperty1("y");
		});

		normalize();
		assertManiCount(2);

		assertPositions(-1, 0, 1);
	}

	@Test
	public void multipleSettingOfSameProperty_Persistent() {
		final TestEntity entity = session.create(TestEntity.T);
		entity.setId(1L);

		record(session -> {
			entity.setProperty1("x");
			entity.setProperty2("");
			entity.setProperty1("y");
		});

		normalize();
		assertManiCount(2);

		assertPositions(-1, 0, 1);
	}

	@Test
	public void keepsNonNormalizableManipulations() {
		record(session -> {
			// nothing
		});

		recordedManipulations.add(manifestation(null));
		recordedManipulations.add(absentingManipulation());

		normalize();
		assertManiCount(2);
	}

	private AbsentingManipulation absentingManipulation() {
		return absenting(null, EntityProperty.T.create());
	}

}
