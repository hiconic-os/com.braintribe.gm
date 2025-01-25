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
 * Marker interface for a base for a proper {@link ServiceProcessor} implementation, with individual requests being processed by methods annotated
 * with {@link Service}.
 * <p>
 * Only valid methods can be annotated. Each method:
 * <ul>
 * <li>The method must return a result, i.e. cannot return void.
 * <li>The method return type must be GM value, or a Maybe wrapping a GM value.
 * <li>The first parameter, which is MANDATORY, must be a {@link ServiceRequest}.
 * <li>The second parameter, which is OPTIONAL, must be the {@link ServiceRequestContext}.
 * </ul>
 * <p>
 * Example:
 * 
 * <pre>
 * public class MyServiceProcessor implements MappingServiceProcessor&lt;TextRequest, String&gt; {
 * 
 * 	&#64;Service
 * 	public String toUpperCase(ToUpperCase request) {
 * 		return request.getText().toUpperCase();
 * 	}
 * 
 * 	&#64;Service
 * 	public String sayHi(SayHi request, ServiceRequestContext context) {
 * 		String name = request.getName();
 * 		if (name == null)
 * 			name = getUsername(context);
 * 
 * 		return "Hi " + name;
 * 	}
 * }
 * </pre>
 * 
 * @param <P>
 *            request (parameter) type
 * @param <R>
 *            response type
 */
@SuppressWarnings("unused")
public interface MappingServiceProcessor<P extends ServiceRequest, R> {
	// empty
}
