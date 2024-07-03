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
package com.braintribe.model.processing.manipulation.basic.oracle;

import static com.braintribe.utils.lcd.CollectionTools2.newList;

import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.braintribe.model.accessapi.ManipulationResponse;
import com.braintribe.model.descriptive.HasName;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.value.EntityReference;
import com.braintribe.model.processing.manipulation.basic.oracle.base.ManipulationToolsTestBase;
import com.braintribe.model.processing.manipulation.testdata.manipulation.TestEntity;
import com.braintribe.model.processing.session.impl.persistence.PersistenceSessionReferenceResolver;

/**
 * Tests for {@link BasicManipulationOracle}
 * 
 * This tests just the reference resolution, as there is nothing else interesting enough o test. At least not right now.
 * 
 * @author peter.gazdik
 */
public class BasicManipulationOracleTests extends ManipulationToolsTestBase {

	private static final int QUERY_BULK_SIZE = 2;

	private final List<GenericEntity> entities = newList();
	private final List<EntityReference> references = newList();

	@Test
	public void single() {
		record(() -> {
			newEntity(TestEntity.T, "e1");
		});

		assertResolve("e1");
	}

	@Test
	public void single_ExplicitId() {
		record(() -> {
			TestEntity e1 = newEntity(TestEntity.T, "e1");
			e1.setId(5);
		});

		assertResolve("e1");
	}

	@Test
	public void single_ExplicitPartition() {
		record(() -> {
			TestEntity e1 = newEntity(TestEntity.T, "e1");
			e1.setPartition("custom");
		});

		assertResolve("e1");
	}

	@Test
	public void single_ExplicitIdAndPartition() {
		record(() -> {
			TestEntity e1 = newEntity(TestEntity.T, "e1");
			setIdentifiers(e1, 87, "custom");
		});

		assertResolve("e1");
	}

	// #############################################
	// ## . . . . . . . . Helpers . . . . . . . . ##
	// #############################################

	protected void record(Runnable r) {
		manipulation = md.track(s -> r.run(), session);
		adjustReferences();

		ManipulationResponse mr = session.commit();

		inducedManipulation = mr.getInducedManipulation();
	}

	private <T extends HasName> T newEntity(EntityType<T> et, String name) {
		T entity = session.create(et);
		entity.setName(name);

		entities.add(entity);
		references.add(entity.reference());

		return entity;
	}

	private void setIdentifiers(GenericEntity entity, Integer id, String partition) {
		entity.setId(id);
		entity.setPartition(partition);
	}

	private void adjustReferences() {
		for (int i = 0; i < entities.size(); i++) {
			GenericEntity entity = entities.get(i);
			EntityReference ref = references.get(i);

			ref.setRefPartition(entity.getPartition());
		}
	}

	private void assertResolve(String... expectedNames) {
		BasicManipulationOracle oracle = newOracle();

		oracle.resolveAll(references);

		List<String> resolvedNames = references.stream() //
				.map(oracle::getResolved) //
				.map(HasName.class::cast) //
				.map(HasName::getName) //
				.collect(Collectors.toList());

		Assertions.assertThat(resolvedNames).containsExactly(expectedNames);
	}

	private BasicManipulationOracle newOracle() {
		BasicManipulationOracle oracle = new BasicManipulationOracle(manipulation, newReferenceResolver());
		oracle.setInducedManipulation(inducedManipulation);

		return oracle;
	}

	private PersistenceSessionReferenceResolver newReferenceResolver() {
		return new PersistenceSessionReferenceResolver(newSession(), s -> null, QUERY_BULK_SIZE);
	}

}
