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
import com.braintribe.model.service.api.result.MulticastResponse;

/**
 * A {@link GenericProcessingRequest} that wraps a request which is to be multicasted and processed by multiple nodes in a clustered runtime
 * environment.
 * 
 * @see UnicastRequest
 */
public interface MulticastRequest extends AuthorizedRequest, NonInterceptableRequest, GenericProcessingRequest, HasServiceRequest {

	EntityType<MulticastRequest> T = EntityTypes.T(MulticastRequest.class);

	/**
	 * This property allows to create a wildcarded filter to which node/application the request should be addressed.
	 */
	InstanceId getAddressee();
	void setAddressee(InstanceId addressee);

	/**
	 * This property holds the information who was sending the request. It will be automatically filled in by the evaluation framework
	 */
	InstanceId getSender();
	void setSender(InstanceId sender);

	/**
	 * The amount of milliseconds to wait for the expected amount of answers
	 */
	Long getTimeout();
	void setTimeout(Long timeout);

	/**
	 * This flag controls if the multicast is to be evaluated asynchronously which means that there is no waiting for the results.
	 * 
	 * @deprecated wrap your {@link MulticastRequest} in an {@link AsynchronousRequest} or use
	 *             {@link EvalContext#get(com.braintribe.processing.async.api.AsyncCallback)} instead
	 */
	@Deprecated
	boolean getAsynchronous();
	/** @deprecated see {@link #getAsynchronous()} */
	@Deprecated
	void setAsynchronous(boolean value);

	@Override
	EvalContext<? extends MulticastResponse> eval(Evaluator<ServiceRequest> evaluator);

	@Override
	default boolean system() {
		return true;
	}

}
