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
package com.braintribe.model.access.smood.collaboration.tools;

import com.braintribe.model.cortexapi.access.collaboration.GetCollaborativeInitializers;
import com.braintribe.model.cortexapi.access.collaboration.GetCollaborativeStageStats;
import com.braintribe.model.cortexapi.access.collaboration.MergeCollaborativeStage;
import com.braintribe.model.cortexapi.access.collaboration.PushCollaborativeStage;
import com.braintribe.model.cortexapi.access.collaboration.RenameCollaborativeStage;
import com.braintribe.model.cortexapi.access.collaboration.ResetCollaborativePersistence;

/**
 * @author peter.gazdik
 */
public class CollaborativePersistenceRequestBuilder {

	public static RenameCollaborativeStage renameStageRequest(String oldName, String newName) {
		RenameCollaborativeStage result = RenameCollaborativeStage.T.create();
		result.setOldName(oldName);
		result.setNewName(newName);

		return result;
	}

	public static PushCollaborativeStage pushStageRequest(String name) {
		PushCollaborativeStage result = PushCollaborativeStage.T.create();
		result.setName(name);

		return result;
	}

	public static MergeCollaborativeStage mergeStageRequest(String source, String target) {
		MergeCollaborativeStage result = MergeCollaborativeStage.T.create();
		result.setSource(source);
		result.setTarget(target);
	
		return result;
	}

	public static ResetCollaborativePersistence resetCollaborativePersistence() {
		return ResetCollaborativePersistence.T.create();
	}

	public static GetCollaborativeStageStats getStageStatsRequest(String name) {
		GetCollaborativeStageStats result = GetCollaborativeStageStats.T.create();
		result.setName(name);
	
		return result;
	}

	public static GetCollaborativeInitializers getInitializersRequest() {
		return GetCollaborativeInitializers.T.create();
	}


}
