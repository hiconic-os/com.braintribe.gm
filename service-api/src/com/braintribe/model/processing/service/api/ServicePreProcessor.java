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
 * A service for processing {@link ServiceRequest} instances before the target
 * {@link ServiceProcessor#process(ServiceRequestContext, ServiceRequest)} method is invoked.
 * 
 * @author dirk.scheffler
 *
 * @param <P>
 *            The type of the request to be pre-processed.
 */
public interface ServicePreProcessor<P extends ServiceRequest> extends ServiceInterceptorProcessor {

	/**
	 * <p>
	 * Pre-processes the given {@link ServiceRequest} instance.
	 * 
	 * <p>
	 * Implementations can override the incoming {@code request} by returning a non-null {@link ServiceRequest}.
	 * 
	 * @param requestContext
	 *            The {@link ServiceRequestContext} of the incoming processing request.
	 * @param request
	 *            The {@link ServiceRequest} to be pre-processed.
	 * @return The overridden {@link ServiceRequest} instance or {@code null} in the case the incoming request is not to
	 *         be overridden.
	 */
	P process(ServiceRequestContext requestContext, P request);

	default @Override InterceptorKind getKind() { return InterceptorKind.pre; }
}
