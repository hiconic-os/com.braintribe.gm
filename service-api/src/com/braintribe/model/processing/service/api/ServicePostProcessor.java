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
import com.braintribe.model.service.api.result.PostProcessResponse;

/**
 * <p>
 * A service for post-processing the results of a {@link ServiceProcessor#process(ServiceRequestContext, ServiceRequest)} call.
 * 
 * @author dirk.scheffler
 *
 * @param <R>
 *            The type of the response to be post-processed.
 */
public interface ServicePostProcessor<R> extends ServiceInterceptorProcessor {

	/**
	 * <p>
	 * Post-processes the result of a {@link ServiceProcessor#process(ServiceRequestContext, ServiceRequest)} call.
	 * 
	 * <p>
	 * Implementations can override the incoming {@code response} by returning a
	 * {@link com.braintribe.model.service.api.result.OverridingPostProcessResponse}.
	 * 
	 * @param requestContext
	 *            The {@link ServiceRequestContext} of the incoming processing request.
	 * @param response
	 *            The {@link ServiceProcessor} response to be post-processed.
	 * @return {@link PostProcessResponse} or {@code null} in the case the incoming response is not to be overridden,
	 *         {@link com.braintribe.model.service.api.result.OverridingPostProcessResponse} otherwise.
	 */
	Object process(ServiceRequestContext requestContext, R response);

	default @Override InterceptorKind getKind() {
		return InterceptorKind.post;
	}
}
