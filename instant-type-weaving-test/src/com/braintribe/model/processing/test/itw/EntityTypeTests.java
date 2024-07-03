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
package com.braintribe.model.processing.test.itw;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.manipulation.Manipulation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelTypeReflection;
import com.braintribe.model.generic.value.EntityReference;
import com.braintribe.model.generic.value.PersistentEntityReference;
import com.braintribe.model.generic.value.PreliminaryEntityReference;
import com.braintribe.model.processing.ImportantItwTestSuperType;
import com.braintribe.model.processing.test.itw.entity.GettersOnlyEntity;
import com.braintribe.model.processing.test.itw.entity.TestEntity;

/**
 * Tests for methods of {@link EntityType}
 */
public class EntityTypeTests extends ImportantItwTestSuperType {

	@Test
	public void typeNamesAreCorrect() {
		assertThat(GenericEntity.T.getTypeSignature()).isEqualTo(GenericEntity.class.getName());
		assertThat(GenericEntity.T.getTypeName()).isEqualTo(GenericEntity.class.getName());
		assertThat(GenericEntity.T.getShortName()).isEqualTo(GenericEntity.class.getSimpleName());
	}

	@Test
	public void abstractEntityIsAbstract() {
		assertThat(GenericEntity.T.isAbstract()).isTrue();
		assertThat(TestEntity.T.isAbstract()).isFalse();
	}

	@Test
	public void assignability() {
		assertThat(GenericEntity.T.isAssignableFrom(TestEntity.T)).isTrue();
		assertThat(TestEntity.T.isAssignableFrom(GenericEntity.T)).isFalse();
	}

	@Test
	public void instanceofChecking() {
		TestEntity entity = TestEntity.T.create();

		assertThat(GenericEntity.T.isInstance(entity)).isTrue();
		assertThat(TestEntity.T.isInstance(entity)).isTrue();
		assertThat(Manipulation.T.isInstance(entity)).isFalse();
	}

	@Test
	public void gettersOnlyEntityWorks() {
		assertThat(GettersOnlyEntity.T.isAbstract()).isFalse();
	}

	@Test
	public void correctPreliminaryReference() {
		TestEntity entity = TestEntity.T.create();
		entity.setPartition("part");

		EntityReference ref = entity.reference();

		assertThat(ref).isInstanceOf(PreliminaryEntityReference.class);
		assertThat(ref.getTypeSignature()).isEqualTo(TestEntity.class.getName());
		assertThat(ref.getRefPartition()).isEqualTo("part");
	}

	@Test
	public void correctPersistentReference() {
		TestEntity entity = TestEntity.T.create();
		entity.setId(99L);
		entity.setPartition("part");

		EntityReference ref = entity.reference();

		assertThat(ref).isInstanceOf(PersistentEntityReference.class);
		assertThat(ref.getRefId()).isEqualTo(99L);
		assertThat(ref.getTypeSignature()).isEqualTo(TestEntity.class.getName());
		assertThat(ref.getRefPartition()).isEqualTo("part");
	}

	@Test
	public void modelSetCorrectly() {
		assertThat(GenericEntity.T.getModel()).isNotNull();
		assertThat(GenericEntity.T.getModel().getModelArtifactDeclaration().getName()).isEqualTo(GenericModelTypeReflection.rootModelName);
	}

}
