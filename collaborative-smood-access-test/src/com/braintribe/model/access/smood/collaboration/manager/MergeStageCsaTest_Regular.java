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

import static com.braintribe.model.access.smood.collaboration.manager.GetStageStatsCsaTest.assertStats;
import static com.braintribe.model.access.smood.collaboration.manager.GetStageStatsCsaTest.assertStatsIsEmpty;
import static com.braintribe.utils.lcd.CollectionTools2.asList;
import static com.braintribe.utils.lcd.CollectionTools2.asSet;

import org.junit.Test;

import com.braintribe.model.access.smood.collaboration.manager.model.StagedEntity;
import com.braintribe.model.cortexapi.access.collaboration.CollaborativeStageStats;

/**
 * 
 */
public class MergeStageCsaTest_Regular extends MergeStageCsaTest_Common {

	@Override
	protected boolean useCortexSetup() {
		return false;
	}

	// ###############################################################
	// ## . . . . . Testing stats are correct after merge . . . . . ##
	// ###############################################################

	/** @see #prePopulateOldStageAndCreateNewTrunk() */
	@Test
	public void onlyPrePopulatedEntityInTrunk() {
		// Old has 1 entity and trunk is empty

		// Assertions
		CollaborativeStageStats stats = getStageStats(oldStageName);
		assertStats(stats, 1, 1, 0);

		stats = getStageStats(trunkStageName);
		assertStatsIsEmpty(stats);
	}

	@Test
	public void trunkStatsAreCorrectAfterNewEntityEntered() {
		CollaborativeStageStats stats;

		// Old has 1 entity and we create one in trunk
		session.create(StagedEntity.T);
		session.commit();

		// check pre-merge stats
		stats = getStageStats(oldStageName);
		assertStats(stats, 1, 1, 0);

		// Merge
		mergeStage(trunkStageName, oldStageName);

		// Assert both instantiations and edits are now in trunkStage
		stats = getStageStats(oldStageName);
		assertStats(stats, 2, 2, 0);

		// And assert that stage1 is empty again
		stats = getStageStats(trunkStageName);
		assertStatsIsEmpty(stats);
	}

	@Test
	public void trunkStatsAreCorrectAfterDelete() {
		// Old has 1 entity and we delete it in trunk
		oldStageEntity = session.query().entity(oldStageEntity).require();
		session.deleteEntity(oldStageEntity);
		session.commit();

		// Merge
		mergeStage(trunkStageName, oldStageName);

		// Assertions
		CollaborativeStageStats stats = getStageStats(oldStageName);
		assertStats(stats, 1, 1, 1 /* one delete here */);
	}

	/** There was a bug with collection manipulation stringifier (remote), which would cause this to fail. */
	@Test
	public void listManipulationIsMergedCorrectly() {
		// create a list manipulation in trunk
		StagedEntity entityWithList = session.create(StagedEntity.T);
		entityWithList.getStringList().addAll(asList("zero", "one"));
		session.commit();

		// Merge
		mergeStage(trunkStageName, oldStageName);

		// Assertions
		assertOldOnly_DataOnly();
		assertOldDataFileContains(".stringList+{0:'zero',1:'one'}");
	}

	/**
	 * Like {@link #listManipulationIsMergedCorrectly()}, but that one can work accidentally because map (value used for
	 * List manipulation description) is the default type to take when model oracle is not configured.
	 */
	@Test
	public void settManipulationIsMergedCorrectly() {
		// create a set manipulation in trunk
		StagedEntity entityWithList = session.create(StagedEntity.T);
		entityWithList.getStringSet().addAll(asSet("zero", "one"));
		session.commit();

		// Merge
		mergeStage(trunkStageName, oldStageName);

		// Assertions
		assertOldOnly_DataOnly();
		assertSetMergedCorectly();
	}
	
	protected void assertSetMergedCorectly() {
		assertOldDataFileContains(".stringSet+('zero','one')");
	}

}
