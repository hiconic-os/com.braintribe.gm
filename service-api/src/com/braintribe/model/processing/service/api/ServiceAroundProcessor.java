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
package com.braintribe.model.processing.service.api;

import com.braintribe.model.service.api.ServiceRequest;

/**
 * <p>
 * A service for wrapping {@link ServiceProcessor#process(ServiceRequestContext, ServiceRequest)} calls.
 *
 * @author dirk.scheffler
 *
 * @param <P>
 *            The type of the request.
 * @param <R>
 *            The type of the response.
 */
public interface ServiceAroundProcessor<P extends ServiceRequest, R extends Object> extends ServiceInterceptorProcessor {

	/**
	 * <p>
	 * Processes the given {@link ServiceRequest} instance offering control over the in-bound request and out-bound
	 * response.
	 * 
	 * <p>
	 * Implementations delegate the call to the next processor in the chain, which might be another
	 * {@link ServiceAroundProcessor}, by calling {@link ProceedContext#proceed(ServiceRequest)} on the given
	 * {@code proceedContext}.
	 * 
	 * <p>
	 * Thus {@code ServiceAroundProcessor} implementations can override the request, override the response and change
	 * the request flow at will.
	 * 
	 * <p>
	 * e.g.:
	 * 
	 * <pre>
	 * public MyResponse process(ServiceRequestContext requestContext, MyRequest request, ProceedContext proceedContext) throws ServiceProcessorException {
	 * 
	 * 	if (overrideRequestCondition) {
	 * 		request = overrideRequest(request);
	 * 	}
	 * 
	 * 	MyResponse response = null;
	 * 
	 * 	if (flowChangeCondition) {
	 * 		response = callSomeThingElse(request);
	 * 	} else {
	 * 		response = proceedContext.proceed(request);
	 * 	}
	 * 
	 * 	if (overrideResponseCondition) {
	 * 		response = overrideResponse(response);
	 * 	}
	 * 
	 * 	return response;
	 * 
	 * }
	 * </pre>
	 * 
	 * @param requestContext
	 *            The {@link ServiceRequestContext} of the incoming processing request.
	 * @param request
	 *            The service request.
	 * @param proceedContext
	 *            The {@link ProceedContext} of the incoming wrapped request.
	 * @return The service response.
	 */
	R process(ServiceRequestContext requestContext, P request, ProceedContext proceedContext);

	default @Override InterceptorKind getKind() { return InterceptorKind.around; }
}
