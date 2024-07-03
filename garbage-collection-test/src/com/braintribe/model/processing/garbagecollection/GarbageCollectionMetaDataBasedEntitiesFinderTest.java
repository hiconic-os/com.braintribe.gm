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
package com.braintribe.model.processing.garbagecollection;

import static com.braintribe.testing.junit.assertions.assertj.core.api.Assertions.assertThat;

import static com.braintribe.utils.lcd.CollectionTools2.newSet;

import java.util.Set;

import org.junit.Test;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.meta.GmEntityType;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.meta.data.cleanup.GarbageCollection;
import com.braintribe.model.meta.data.cleanup.GarbageCollectionKind;
import com.braintribe.model.processing.meta.oracle.BasicModelOracle;
import com.braintribe.testing.model.test.technical.features.CollectionEntity;
import com.braintribe.testing.model.test.technical.features.ComplexEntity;
import com.braintribe.testing.model.test.technical.features.SimpleEntity;
import com.braintribe.testing.model.test.testtools.TestModelTestTools;

/**
 * Provides tests for {@link GarbageCollectionMetaDataBasedEntitiesFinder}.
 *
 * @author michael.lafite
 */
public class GarbageCollectionMetaDataBasedEntitiesFinderTest {

	@Test
	public void testFindEntityTypes() {

		final GmMetaModel metaModel = TestModelTestTools.createTestModelMetaModel();

		BasicModelOracle modelOracle = new BasicModelOracle(metaModel);
		
		final GmEntityType genericEntityType = modelOracle.getEntityTypeOracle(GenericEntity.T).asGmEntityType();
		addGarbageCollectionMetaData(genericEntityType, GarbageCollectionKind.hold);

		final GmEntityType complexEntityType = modelOracle.getEntityTypeOracle(ComplexEntity.T).asGmEntityType();
		addGarbageCollectionMetaData(complexEntityType, GarbageCollectionKind.anchor);

		final GmEntityType simpleEntityType = modelOracle.getEntityTypeOracle(SimpleEntity.T).asGmEntityType();
		addGarbageCollectionMetaData(simpleEntityType, GarbageCollectionKind.collect);

		final Set<String> holdTypes = GarbageCollectionMetaDataBasedEntitiesFinder.findEntityTypes(metaModel,
				GarbageCollectionKind.hold, null);
		assertThat(holdTypes).contains(GenericEntity.class.getName());
		// inherited
		assertThat(holdTypes).contains(CollectionEntity.class.getName());
		// other types
		assertThat(holdTypes).doesNotContain(SimpleEntity.class.getName(), ComplexEntity.class.getName());
	}

	private static void addGarbageCollectionMetaData(final GmEntityType entityType,
			final GarbageCollectionKind garbageCollectionKind) {
		final GarbageCollection garbageCollection = GarbageCollection.T.create();
		garbageCollection.setKind(garbageCollectionKind);
		if (entityType.getMetaData() == null) {
			entityType.setMetaData(newSet());
		}
		entityType.getMetaData().add(garbageCollection);
	}
}
