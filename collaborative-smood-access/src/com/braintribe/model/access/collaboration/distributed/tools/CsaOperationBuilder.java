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
package com.braintribe.model.access.collaboration.distributed.tools;

import com.braintribe.model.access.collaboration.distributed.api.model.CsaDeleteResource;
import com.braintribe.model.access.collaboration.distributed.api.model.CsaManagePersistence;
import com.braintribe.model.access.collaboration.distributed.api.model.CsaResourceBasedOperation;
import com.braintribe.model.access.collaboration.distributed.api.model.CsaStoreResource;
import com.braintribe.model.cortexapi.access.collaboration.CollaborativePersistenceRequest;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.resource.Resource;

/**
 * @author peter.gazdik
 */
public interface CsaOperationBuilder {

	static CsaManagePersistence managePersistence(CollaborativePersistenceRequest request) {
		CsaManagePersistence result = CsaManagePersistence.T.create();
		result.setPersistenceRequest(request);

		return result;
	}

	// The ignoredResource is there to allow for a functional interface compatible with this and storeResource
	static CsaDeleteResource deleteResource(@SuppressWarnings("unused") Resource ignoredResource, String resourceRelativePath) {
		CsaDeleteResource result = CsaDeleteResource.T.create();
		result.setResourceRelativePath(resourceRelativePath);

		return result;
	}

	static CsaStoreResource storeResource(Resource payload, String resourceRelativePath) {
		CsaStoreResource result = resourceBasedOp(CsaStoreResource.T, payload);
		result.setResourceRelativePath(resourceRelativePath);

		return result;
	}

	static <O extends CsaResourceBasedOperation> O resourceBasedOp(EntityType<O> operationType, Resource payload) {
		O result = operationType.create();
		result.setPayload(payload);

		return result;
	}

}
