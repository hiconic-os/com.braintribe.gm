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
	 * Address of the nodes/applications the request should be addressed.
	 * <p>
	 * Both {@link InstanceId#getApplicationId() applicationId} and {@link InstanceId#getNodeId() nodeId} can be <code>null</code>, which is
	 * interpreted as "any".
	 */
	InstanceId getAddressee();
	void setAddressee(InstanceId addressee);

	/** {@link InstanceId} of the sender. It is automatically filled in by the evaluation framework */
	InstanceId getSender();
	void setSender(InstanceId sender);

	/**
	 * The amount of milliseconds to wait for the expected amount of answers.
	 * <p>
	 * Overrides the default timeout configured on the (multicast) processor.
	 * <p>
	 * A processor is expected to know the number of nodes in the cluster, and when it sends a request, it waits until all the responses are sent back
	 * (see {@link MulticastResponse}) or until it times out, in which case it returns with the results it has gotten so far.
	 * <p>
	 * There is no indicator on {@link MulticastResponse} that this waiting timed out :(.
	 */
	Long getTimeout();
	void setTimeout(Long timeout);

	/**
	 * If <tt>true</tt>, the nested {@link #getServiceRequest() request} will only be broadcasted and no response will be sent back, thus sparing
	 * resources.
	 * <p>
	 * Typically, the (multicast) processor waits for the answers from all the known nodes (up to a set {@link #getTimeout() timeout}), and returns a
	 * {@link MulticastResponse} with all the collected results.
	 * <p>
	 * For asynchronous multicast requests, it broadcasts the nested request and immediately returns <tt>null</tt>, without waiting for any response.
	 */
	boolean getAsynchronous();
	void setAsynchronous(boolean value);

	@Override
	EvalContext<? extends MulticastResponse> eval(Evaluator<ServiceRequest> evaluator);

	@Override
	default boolean system() {
		return true;
	}

}
