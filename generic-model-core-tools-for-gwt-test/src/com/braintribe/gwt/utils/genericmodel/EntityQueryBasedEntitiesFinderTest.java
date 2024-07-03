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
package com.braintribe.gwt.utils.genericmodel;

import static com.braintribe.testing.junit.assertions.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.braintribe.model.access.IncrementalAccess;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.session.exception.GmSessionException;
import com.braintribe.model.processing.query.fluent.EntityQueryBuilder;
import com.braintribe.model.processing.session.impl.persistence.BasicPersistenceGmSession;
import com.braintribe.model.query.EntityQuery;
import com.braintribe.testing.model.test.technical.features.CollectionEntity;
import com.braintribe.testing.model.test.technical.features.ComplexEntity;
import com.braintribe.testing.model.test.technical.features.SimpleEntity;
import com.braintribe.testing.model.test.technical.features.SimpleTypesEntity;
import com.braintribe.testing.model.test.testtools.TestModelTestTools;

public class EntityQueryBasedEntitiesFinderTest {

	private BasicPersistenceGmSession session = null;
	private final int expectedSizeForSimpleEntityQuery1 = 2;
	private final int expectedSizeForSimpleEntityQuery2 = 1;

	@Before
	public void setUp() throws GmSessionException {

		final IncrementalAccess access = TestModelTestTools.newSmoodAccessMemoryOnly("test", TestModelTestTools.createTestModelMetaModel());
		this.session = new BasicPersistenceGmSession();
		this.session.setIncrementalAccess(access);

		final SimpleEntity simpleEntity1 = this.session.create(SimpleEntity.T);
		simpleEntity1.setBooleanProperty(false);
		simpleEntity1.setStringProperty("StringProperty1");

		final SimpleEntity simpleEntity2 = this.session.create(SimpleEntity.T);
		simpleEntity2.setBooleanProperty(true);
		simpleEntity2.setStringProperty("StringProperty2");

		final SimpleEntity simpleEntity3 = this.session.create(SimpleEntity.T);
		simpleEntity3.setBooleanProperty(false);
		simpleEntity3.setStringProperty("StringProperty3");

		final ComplexEntity complexEntity = this.session.create(ComplexEntity.T);
		complexEntity.setSimpleEntityProperty(simpleEntity1);
		complexEntity.setBooleanProperty(true);
		complexEntity.setStringProperty("StringPropertC1");

		final SimpleTypesEntity simpleTypesEntity = this.session.create(SimpleTypesEntity.T);
		simpleTypesEntity.setBooleanProperty(true);
		simpleTypesEntity.setDateProperty(new Date());
		simpleTypesEntity.setStringProperty("StringPropertSTE1");

		final SimpleTypesEntity simpleTypesEntity2 = this.session.create(SimpleTypesEntity.T);
		simpleTypesEntity2.setBooleanProperty(true);
		simpleTypesEntity2.setDateProperty(new Date());
		simpleTypesEntity2.setStringProperty("StringPropertSTE2");

		final CollectionEntity collectionEntity = this.session.create(CollectionEntity.T);
		final List<SimpleEntity> simpleEntityList = new ArrayList<SimpleEntity>();
		simpleEntityList.add(simpleEntity1);
		simpleEntityList.add(simpleEntity2);
		collectionEntity.setSimpleEntityList(simpleEntityList);
		this.session.commit();
	}

	@Test
	public void testQueryBasedEntitiesFinderQuery1() {

		//@formatter:off
		final EntityQuery entityQuery = EntityQueryBuilder.from(SimpleEntity.class)
					.where().property("booleanProperty").eq(false)
				.done();
		//@formatter:on

		final EntityQueryBasedEntitiesFinder entitiesFinder = new EntityQueryBasedEntitiesFinder();
		entitiesFinder.setEntityQuery(entityQuery);

		final Set<GenericEntity> foundEntities = entitiesFinder.findEntities(this.session);
		assertThat(foundEntities.size()).as("The size of the founded entities")
				.isEqualTo(this.expectedSizeForSimpleEntityQuery1);
	}

	@Test
	public void testQueryBasedEntitiesFinderQuery2() {

		//@formatter:off
		final EntityQuery entityQuery = EntityQueryBuilder.from(SimpleEntity.class).where()
				.conjunction()
					.property("stringProperty").eq("StringProperty1")
					.property("booleanProperty").eq(false)
				.close().done();
		//@formatter:on

		final EntityQueryBasedEntitiesFinder entitiesFinder = new EntityQueryBasedEntitiesFinder();
		entitiesFinder.setEntityQuery(entityQuery);

		final Set<GenericEntity> foundEntities = entitiesFinder.findEntities(this.session);
		assertThat(foundEntities.size()).as("The size of the founded entities")
				.isEqualTo(this.expectedSizeForSimpleEntityQuery2);
	}
}
