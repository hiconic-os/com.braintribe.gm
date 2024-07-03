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
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * <p>
 * A {@link GenericProcessingRequest} wraps a request which is to be processed by a specific node in a clustered runtime environment which is the opposite
 * of the normal route which involves a load balancer
 * {@link #setAddressee(InstanceId)}.
 */
public interface UnicastRequest extends AuthorizedRequest, NonInterceptableRequest, GenericProcessingRequest, HasServiceRequest {

	EntityType<UnicastRequest> T = EntityTypes.T(UnicastRequest.class);

	/**
	 * The description of the node that should execute the wrapped request
	 */
	InstanceId getAddressee();
	void setAddressee(InstanceId addressee);

	/**
	 * The time to wait for the request to be exectued synchronously as unicasts are often implemented by using messasge queues.
	 */
	Long getTimeout();
	void setTimeout(Long timeout);

	/**
	 * Determines if the wrapped request will be executed in an asynchronous mode which means that there is no waiting for a response
	 * @deprecated use {@link AsynchronousRequest} or {@link EvalContext#get(com.braintribe.processing.async.api.AsyncCallback)} instead  
	 */
	@Deprecated
	boolean getAsynchronous();
	
	/**
	 * @deprecated use {@link AsynchronousRequest} or {@link EvalContext#get(com.braintribe.processing.async.api.AsyncCallback)} instead  
	 */
	@Deprecated
	void setAsynchronous(boolean value);

	@Override
	default boolean system() {
		return true;
	}

}
