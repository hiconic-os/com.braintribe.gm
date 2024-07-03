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
package com.braintribe.model.access.smood.collaboration.offline;

import static com.braintribe.testing.junit.assertions.gm.assertj.core.api.GmAssertions.assertEntity;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.braintribe.model.access.smood.collaboration.basic.AbstractCollaborativePersistenceTest;
import com.braintribe.model.access.smood.collaboration.manager.model.StagedEntity;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.resource.Resource;

/**
 * @author peter.gazdik
 */
public class AppendOfflineTest extends AbstractCollaborativePersistenceTest {

	private static final String MODEL_SNIPPET = "$e=!com.braintribe.model.meta.GmMetaModel() .globalId='MODEL'";
	private static final String DATA_SNIPPET = "$e=!com.braintribe.model.access.smood.collaboration.manager.model.StagedEntity() .globalId='ENTITY'";

	private static final Resource MODEL_RESOURCE = resourceFor(MODEL_SNIPPET);
	private static final Resource DATA_RESOURCE = resourceFor(DATA_SNIPPET);

	private static Resource resourceFor(String content) {
		return Resource.createTransient(() -> new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
	}

	@Test
	public void appendsDataToEmptyStage() throws Exception {
		csaUnit.acquireOfflineManager().append(new Resource[] { null, DATA_RESOURCE });

		// @formatter:off
		baseFolderFsAssert
			.sub("config.json").isExistingFile_()
			.sub(trunkStageName).isDirectory()
				.sub("model.man").notExists_()
				.sub("data.man") //
					.contains(DATA_SNIPPET);
		// @formatter:on
	}

	@Test
	public void appendsModelToEmptyStage() throws Exception {
		csaUnit.acquireOfflineManager().append(new Resource[] { MODEL_RESOURCE, null });

		// @formatter:off
		baseFolderFsAssert
			.sub("config.json").isExistingFile_()
			.sub(trunkStageName).isDirectory()
				.sub("data.man").notExists_()
				.sub("model.man") //
					.contains(MODEL_SNIPPET);
		// @formatter:on
	}

	@Test
	public void appendsModelAndDataToEmptyStage() throws Exception {
		csaUnit.acquireOfflineManager().append(new Resource[] { MODEL_RESOURCE, DATA_RESOURCE });

		// @formatter:off
		baseFolderFsAssert
			.sub("config.json").isExistingFile_()
			.sub(trunkStageName).isDirectory()
				.sub("model.man") //
					.contains(MODEL_SNIPPET)
				.sup()
				.sub("data.man") //
					.contains(DATA_SNIPPET);
		// @formatter:on

		assertDataAppendedProperly(false);
	}

	@Test
	public void appendsModelAndDataToNonEmptyStage() throws Exception {
		session.create(StagedEntity.T, "ORIGINAL_ENTITY");
		session.create(GmMetaModel.T, "ORIGINAL_MODEL");
		session.commit();

		csaUnit.acquireOfflineManager().append(new Resource[] { MODEL_RESOURCE, DATA_RESOURCE });

		assertDataAppendedProperly(true);
	}

	private void assertDataAppendedProperly(boolean withOriginal) {
		redeploy();

		if (withOriginal) {
			assertEntity(session.findEntityByGlobalId("ORIGINAL_MODEL")).isInstanceOf(GmMetaModel.T);
			assertEntity(session.findEntityByGlobalId("ORIGINAL_ENTITY")).isInstanceOf(StagedEntity.T);
		} else {
			assertEntity(session.findEntityByGlobalId("ORIGINAL_MODEL")).isNull();
			assertEntity(session.findEntityByGlobalId("ORIGINAL_ENTITY")).isNull();
		}

		assertEntity(session.findEntityByGlobalId("MODEL")).isInstanceOf(GmMetaModel.T);
		assertEntity(session.findEntityByGlobalId("ENTITY")).isInstanceOf(StagedEntity.T);
	}

}
