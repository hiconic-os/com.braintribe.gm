// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2026
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
 * Establishes a lexical context around the transport-side processing of an already decoded {@link ServiceRequest}.
 * <p>
 * The context starts before routine request logging and ends after response handling and final request logging. Implementations may use it for
 * cross-cutting request-scoped facilities without coupling transports to those facilities.
 */
@FunctionalInterface
public interface ServiceRequestContextualizer {

	ServiceRequestContextualizer DIRECT = (request, execution) -> execution.execute();

	void contextualize(ServiceRequest request, ServiceRequestExecution execution) throws Exception;
}
