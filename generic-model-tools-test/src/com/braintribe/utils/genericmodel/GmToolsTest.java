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
package com.braintribe.utils.genericmodel;

import static com.braintribe.testing.junit.assertions.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.processing.query.fluent.EntityQueryBuilder;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.processing.session.impl.managed.history.BasicTransaction;
import com.braintribe.testing.category.KnownIssue;
import com.braintribe.testing.model.test.technical.features.CollectionEntity;
import com.braintribe.testing.model.test.technical.features.SimpleEntity;
import com.braintribe.testing.tools.gm.GmTestTools;
import com.braintribe.utils.CollectionTools;
import com.braintribe.utils.ReflectionTools;

/**
 * Provides Tests for {@link GmTools}.
 * 
 * @author michael.lafite
 */
public class GmToolsTest {

	private static String LENIENTDECODINGTEST_FILEPATH = "res/lenientDecodingTest.xml";

	@Test
	public void testDecodeLeniently() throws Exception {
		PersistenceGmSession session = GmTools.decodeLeniently(new File(LENIENTDECODINGTEST_FILEPATH));
		GmMetaModel model = session.query()
				.entities(EntityQueryBuilder.from(GmMetaModel.class).tc().negation().joker().done()).first();
		assertThat(model.getName()).isEqualTo("com.braintribe.model.test:ExampleModel#1.0");
	}

	@Test
	@Category(KnownIssue.class)
	public void testEnsureModelTypes() throws Exception {
		final String lenientDecodingTestEntityTypeTypeSignature = "com.braintribe.model.test.LenientDecodingTestEntityType";
		assertThat(ReflectionTools.classExists(lenientDecodingTestEntityTypeTypeSignature)).isFalse();
		GmTools.ensureModelTypes(new File(LENIENTDECODINGTEST_FILEPATH), "THERE_IS_NO_MODEL_WITH_THIS_NAME");
		assertThat(ReflectionTools.classExists(lenientDecodingTestEntityTypeTypeSignature)).isFalse();
		GmTools.ensureModelTypes(new File(LENIENTDECODINGTEST_FILEPATH), "com.braintribe.model.test:ExampleModel#*");
		assertThat(ReflectionTools.classExists(lenientDecodingTestEntityTypeTypeSignature)).isTrue();

		PersistenceGmSession session = GmTools.decodeLeniently(new File(LENIENTDECODINGTEST_FILEPATH));
		GenericEntity entity = session.query()
				.entities(EntityQueryBuilder.from(lenientDecodingTestEntityTypeTypeSignature).done()).first();
		assertThat(entity).isNotNull();
	}

	@Test
	@Category(KnownIssue.class)
	public void testPrintDoneManipulations() throws Exception {
		PersistenceGmSession session = GmTestTools.newSessionWithSmoodAccessAndTemporaryFile();
		CollectionEntity collectionEntity = session.create(CollectionEntity.T);

		SimpleEntity simpleEntity = session.create(SimpleEntity.T);
		collectionEntity.setSimpleEntitySet(new HashSet<SimpleEntity>());

		session.commit();

		collectionEntity.getSimpleEntitySet().add(simpleEntity);

		List<?> listeners = (List<?>) ReflectionTools.getFieldValue("manipulationListeners", session);
		BasicTransaction transaction = CollectionTools.getSingleElement(listeners, BasicTransaction.class);
		session.commit();
		
		assertThat(transaction).isNotNull();
	}

}
