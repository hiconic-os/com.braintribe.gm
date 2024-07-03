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
 * Managed processor of {@link ServiceRequest} instances.
 * 
 * <h1>Naming convention</h1>
 * 
 * <p>
 * Specializations are encouraged to keep the suffixes {@code ServiceProcessor} (or simply {@code Processor}),
 * {@code Request} and {@code Response} for their respective {@link ServiceProcessor}, {@link ServiceRequest} and
 * response types, thus following signature pattern:
 * 
 * <p>
 * <i>{purpose}</i>ServiceProcessor implements ServiceProcessor<<i>{purpose}</i>Request, <i>{purpose}</i>Response>
 * 
 * <p>
 * e.g.: <br>
 * {@code SecurityServiceProcessor implements ServiceProcessor<SecurityRequest, SecurityResponse>}
 * 
 * <p>
 * For processors leveraging on polymorphism to address multiple tasks, request specializations may be named like
 * functions.
 * 
 * <p>
 * e.g.:
 * <ul>
 * <li>{@code Logout extends SecurityRequest}</li>
 * <li>{@code OpenUserSession extends SecurityRequest}</li>
 * <li>{@code ValidateUserSession extends SecurityRequest}</li>
 * </ul>
 * 
 * @author dirk.scheffler
 * 
 * @param <P>
 *            The input ({@link ServiceRequest}) type
 * @param <R>
 *            The return type
 */
public interface ServiceProcessor<P extends ServiceRequest, R extends Object> {

	/**
	 * <p>
	 * Processes the given {@code request}.
	 * 
	 * @param requestContext
	 *            A {@link ServiceRequestContext} providing contextual information about the request.
	 * @param request
	 *            The {@link ServiceRequest} to be processed.
	 * @return The result of the {@link ServiceRequest} processing.
	 */
	R process(ServiceRequestContext requestContext, P request);

}
