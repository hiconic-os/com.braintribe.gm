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

import static com.braintribe.gm.model.reason.UnsatisfiedMaybeTunneling.getOrTunnel;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.model.service.api.ServiceRequest;

/**
 * ReasonedServiceAroundProcessor is  trait like interface that adapts the {@link Maybe} returning
 * {@link #processReasoned(ServiceRequestContext, ServiceRequest, ProceedContext)} to the {@link ServiceAroundProcessor#process(ServiceRequestContext, ServiceRequest, ProceedContext)}
 * method.
 * 
 * @author Dirk Scheffler
 */
public interface ReasonedServiceAroundProcessor<P extends ServiceRequest, R> extends ServiceAroundProcessor<P, R> {
	@Override
	default R process(ServiceRequestContext requestContext, P request, ProceedContext proceedContext) {
		return getOrTunnel(processReasoned(requestContext, request, proceedContext));
	}
	
	Maybe<? extends R> processReasoned(ServiceRequestContext context, P request, ProceedContext proceedContext);
}
