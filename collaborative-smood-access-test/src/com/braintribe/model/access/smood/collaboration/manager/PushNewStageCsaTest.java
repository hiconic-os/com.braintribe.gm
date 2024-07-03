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

import org.junit.Test;

import com.braintribe.model.access.collaboration.CollaborativeAccessManager;
import com.braintribe.model.access.smood.collaboration.manager.model.StagedEntity;
import com.braintribe.model.cortexapi.access.collaboration.PushCollaborativeStage;
import com.braintribe.testing.junit.assertions.assertj.core.api.Assertions;

/**
 * @see PushCollaborativeStage
 * @see CollaborativeAccessManager
 * 
 * @author peter.gazdik
 */
public class PushNewStageCsaTest extends AbstractCollaborativeAccessManagerTest {

	final String newStageName = "newStage";

	@Test
	public void pushStage() {
		StagedEntity entity = session.create(StagedEntity.T);
		session.commit();

		pushNewStage(newStageName);

		StagedEntity entity2 = session.create(StagedEntity.T);
		session.commit();

		assertEntityStage(entity, trunkStageName);
		assertEntityStage(entity2, newStageName);

		// @formatter:off
		baseFolderFsAssert
			.sub("config.json").isExistingFile_()
			.sub(trunkStageName).isDirectory()
				.sub("data.man").isExistingFile_().sup()	
			.sub(newStageName).isDirectory()
				.sub("data.man").isExistingFile_();
		// @formatter:on
	}

	@Test
	public void cannotPushAlreadyExistingStage() {
		pushNewStage(newStageName);
		try {
			pushNewStage(newStageName);
			Assertions.fail("Exception should have been throw as we are trying to push an already existing stage.");

		} catch (Exception e) {
			Assertions.assertThat(e.getMessage()).contains("stage already exists");
		}
	}

}
