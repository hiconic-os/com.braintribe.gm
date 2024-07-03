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
package com.braintribe.model.access.smood.collaboration.manager;

import static com.braintribe.testing.junit.assertions.assertj.core.api.Assertions.assertThat;
import static com.braintribe.utils.lcd.CollectionTools2.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.braintribe.model.access.collaboration.CollaborativeAccessManager;
import com.braintribe.model.access.smood.collaboration.manager.model.StagedEntity;
import com.braintribe.model.cortexapi.access.collaboration.MergeCollaborativeStage;
import com.braintribe.model.csa.SmoodInitializer;
import com.braintribe.model.processing.query.fluent.SelectQueryBuilder;
import com.braintribe.model.query.SelectQuery;
import com.braintribe.testing.junit.assertions.assertj.core.api.Assertions;

/**
 * @see MergeCollaborativeStage
 * @see CollaborativeAccessManager
 *
 * @author peter.gazdik
 */
public abstract class MergeStageCsaTest_Common extends AbstractCollaborativeAccessManagerTest {

	protected static final String oldStageName = "oldStage";

	protected StagedEntity oldStageEntity;

	@Before
	public void prePopulateOldStageAndCreateNewTrunk() {
		oldStageEntity = session.create(StagedEntity.T);
		session.commit();

		renameStage(trunkStageName, oldStageName);
		pushNewStage(trunkStageName);
	}

	@Test
	public void mergeEmptyStage() {
		// Trunk
		// <EMPTY>

		// Merge
		mergeStage(trunkStageName, oldStageName);

		// Assertions
		assertOldOnly_DataOnly();
		assertEntityStage(oldStageEntity, oldStageName);
	}

	@Test
	public void mergeSimpleData() {
		// Trunk
		StagedEntity trunkEntity = session.create(StagedEntity.T);
		session.commit();

		// Merge
		mergeStage(trunkStageName, oldStageName);

		// Assertions
		assertOldDataFileContains("$0", "$1");

		assertOldOnly_DataOnly();
		assertEntityStage(trunkEntity, oldStageName);
	}

	@Test
	public void mergeSimpleDataMultipleTimes() {
		// Trunk
		StagedEntity trunk_E1 = session.create(StagedEntity.T);
		session.commit();

		// Merge
		mergeStage(trunkStageName, oldStageName);

		StagedEntity trunk_E2 = session.create(StagedEntity.T);
		session.commit();

		// Merge
		mergeStage(trunkStageName, oldStageName);

		// Assertions
		assertOldDataFileContains("$0", "$1", "$2");

		assertOldOnly_DataOnly();
		assertEntityStage(trunk_E1, oldStageName);
		assertEntityStage(trunk_E2, oldStageName);
	}

	@Test
	public void nonTrunkStageIsDeletedAfterMerge() {
		final String newerStageName = "newerStage";

		// Trunk (will later be newerStage)
		StagedEntity trunk_E1 = session.create(StagedEntity.T);
		session.commit();

		//
		renameStage(trunkStageName, newerStageName);
		pushNewStage(trunkStageName);

		// Merge
		mergeStage(newerStageName, oldStageName);

		// Assertions
		assertEntityStage(trunk_E1, oldStageName);

		// @formatter:off
		baseFolderFsAssert
			.sub(oldStageName).isDirectory()
				.sub("data.man").isExistingFile_()
				.sup()
			.sub(newerStageName).notExists_()
			.sub(trunkStageName).isEmptyDirectory()
			;
		// @formatter:on

		List<SmoodInitializer> initializers = getInitializers();
		assertThat(initializers).hasSize(2);

		Iterator<SmoodInitializer> it = initializers.iterator();
		assertThat(it.next().getName()).isEqualTo(oldStageName);
		assertThat(it.next().getName()).isEqualTo(trunkStageName);
	}

	// #################################################
	// ## . . . . . . . . . Helpers . . . . . . . . . ##
	// #################################################

	protected SelectQuery queryStagedEntityByName(String name) {
		// @formatter:off
		return new SelectQueryBuilder()
				.from(StagedEntity.T, "e")
				.where()
					.property("e", "name").eq(name)
				.done();
		// @formatter:on
	}

	protected void assertOldOnly_DataOnly() {
		// @formatter:off
		baseFolderFsAssert
			.sub(oldStageName).isDirectory()
				.sub("data.man").isExistingFile_()
				.sup()
			.sub(trunkStageName).isEmptyDirectory()
			;
		// @formatter:on
	}

	protected void assertOldOnly_ModelAndData() {
		// @formatter:off
		baseFolderFsAssert
			.sub(oldStageName).isDirectory()
				.sub("data.man").isExistingFile_()
				.sub("model.man").isExistingFile_()
			.sup()
			.sub(trunkStageName).isEmptyDirectory()
			;
		// @formatter:on
	}

	protected void assertOldDataFileContains(String... values) {
		String oldDataFileContent = getStageFileContent(oldStageName, false);
		Assertions.assertThat(oldDataFileContent).containsAll(values);
	}

	protected void assertOldDataFileNotContains(String... values) {
		String oldDataFileContent = getStageFileContent(oldStageName, false);
		Assertions.assertThat(oldDataFileContent).doesNotContain(asList(values));
	}

}
