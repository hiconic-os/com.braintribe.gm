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
import com.braintribe.model.access.smood.collaboration.manager.RenameStageCsaTest;
import com.braintribe.model.cortexapi.access.collaboration.CollaborativePersistenceRequest;
import com.braintribe.model.cortexapi.access.collaboration.RenameCollaborativeStage;

/**
 * @see RenameCollaborativeStage
 * @see CollaborativeAccessManager
 *
 * @author peter.gazdik
 */
public class RenameStageOfflineTest extends RenameStageCsaTest {

	@Override
	protected <T> T eval(CollaborativePersistenceRequest request) {
		csaUnit.evalOffline(request);
		redeploy();
		return null;
	}

}
