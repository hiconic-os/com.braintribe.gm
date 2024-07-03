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

import com.braintribe.model.access.collaboration.CollaborativeAccessManager;
import com.braintribe.model.access.smood.collaboration.manager.MergeStageCsaTest_Cortex;
import com.braintribe.model.cortexapi.access.collaboration.CollaborativePersistenceRequest;
import com.braintribe.model.cortexapi.access.collaboration.MergeCollaborativeStage;
import com.braintribe.model.cortexapi.access.collaboration.ReadOnlyCollaborativePersistenceRequest;

/**
 * @see MergeStageCsaTest_Cortex
 * @see MergeCollaborativeStage
 * @see CollaborativeAccessManager
 *
 * @author peter.gazdik
 */
public class MergeStageOfflineTest_Cortex extends MergeStageCsaTest_Cortex {

	@Override
	protected <T> T eval(CollaborativePersistenceRequest request) {
		if (request instanceof ReadOnlyCollaborativePersistenceRequest) {
			return csaUnit.eval(request);

		} else {
			csaUnit.evalOffline(request);
			redeploy();
			return null;
		}
	}

}
