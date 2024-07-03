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
package com.braintribe.model.resourceapi.persistence;

import com.braintribe.model.accessapi.AccessDataRequest;
import com.braintribe.model.generic.annotation.Initializer;
import com.braintribe.model.generic.eval.EvalContext;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.resource.Resource;
import com.braintribe.model.service.api.ServiceRequest;

public interface StoreBinary extends BinaryPersistenceRequest, AccessDataRequest {

	EntityType<StoreBinary> T = EntityTypes.T(StoreBinary.class);

	Resource getCreateFrom();
	void setCreateFrom(Resource createFrom);

	/**
	 * Most implementations for this requests used to persist the Resource entity of the {@link StoreBinaryResponse
	 * response} into the access. This should however not be its responsibility but should happen (and from now on will
	 * happen) during {@link UploadResource}. This flag exists just for backwards compatibility reasons but must,
	 * because of this, be explicitly set to false. Please always set it to false and make sure the {@link Resource} is
	 * persisted afterwards (if needed).
	 */
	@Initializer("true")
	boolean getPersistResource();
	void setPersistResource(boolean persistResource);

	@Override
	EvalContext<? extends StoreBinaryResponse> eval(Evaluator<ServiceRequest> evaluator);

}
