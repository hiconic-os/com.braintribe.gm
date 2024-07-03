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
package com.braintribe.model.service.api;

import com.braintribe.model.generic.eval.EvalContext;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.service.api.result.PushResponse;

/**
 * A PushRequest sends its wrapped {@link HasServiceRequest#getServiceRequest() request} to all push notification subscribers that match given
 * addressing.
 */
public interface PushRequest extends PushAddressing, AuthorizedRequest, GenericProcessingRequest, HasServiceRequest {

	EntityType<PushRequest> T = EntityTypes.T(PushRequest.class);

	@Override
	EvalContext<? extends PushResponse> eval(Evaluator<ServiceRequest> evaluator);

}
