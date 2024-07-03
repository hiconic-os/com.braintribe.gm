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
package com.braintribe.gm.service.impl;

import com.braintribe.model.processing.service.api.ProceedContext;
import com.braintribe.model.processing.service.api.ServiceAroundProcessor;
import com.braintribe.model.processing.service.api.ServiceRequestContext;
import com.braintribe.model.processing.service.api.aspect.DomainIdAspect;
import com.braintribe.model.service.api.ServiceRequest;

public class DomainIdServiceAroundProcessor implements ServiceAroundProcessor<ServiceRequest, Object> {

	public final static DomainIdServiceAroundProcessor INSTANCE = new DomainIdServiceAroundProcessor();

	private DomainIdServiceAroundProcessor() {
		// singleton
	}

	@Override
	public Object process(ServiceRequestContext requestContext, ServiceRequest request, ProceedContext proceedContext) {
		ServiceRequestContext contextWithDomainId = requestContext.derive().set(DomainIdAspect.class, request.domainId()).build();
		return proceedContext.proceed(contextWithDomainId, request);
	}

}
