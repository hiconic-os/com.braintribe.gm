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
package com.braintribe.model.cortexapi.access.collaboration;

import com.braintribe.model.generic.annotation.meta.Description;
import com.braintribe.model.generic.eval.EvalContext;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.service.api.ServiceRequest;

@Description("Resets the persistence to it's initial state, deleting any custom data. Configured initial data of given access will be kept.")
public interface ResetCollaborativePersistence extends CollaborativePersistenceRequest {

	EntityType<ResetCollaborativePersistence> T = EntityTypes.T(ResetCollaborativePersistence.class);

	@Override
	EvalContext<Boolean> eval(Evaluator<ServiceRequest> evaluator);

	@Override
	default CollaborativePersistenceRequestType collaborativeRequestType() {
		return CollaborativePersistenceRequestType.Reset;
	}

}
