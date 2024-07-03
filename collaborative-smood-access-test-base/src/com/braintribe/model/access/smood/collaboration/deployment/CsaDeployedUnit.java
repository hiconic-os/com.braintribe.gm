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
package com.braintribe.model.access.smood.collaboration.deployment;

import com.braintribe.model.access.collaboration.CollaborativeSmoodAccess;
import com.braintribe.model.access.collaboration.offline.CollaborativeAccessOfflineManager;
import com.braintribe.model.cortexapi.access.collaboration.CollaborativePersistenceRequest;

/**
 * @author peter.gazdik
 */
public class CsaDeployedUnit extends AbstractCsaDeployedUnit<CollaborativeSmoodAccess> {

	private CollaborativeAccessOfflineManager offlineManager;

	public void evalOffline(CollaborativePersistenceRequest request) {
		acquireOfflineManager().process(null, request);
	}

	public CollaborativeAccessOfflineManager acquireOfflineManager() {
		if (offlineManager == null)
			offlineManager = newOfflineManager();

		return offlineManager;
	}

	private CollaborativeAccessOfflineManager newOfflineManager() {
		CollaborativeAccessOfflineManager result = new CollaborativeAccessOfflineManager();
		result.setBaseFolder(baseFolder);
		result.setCsaStatePersistence(statePersistence);

		return result;
	}

}
