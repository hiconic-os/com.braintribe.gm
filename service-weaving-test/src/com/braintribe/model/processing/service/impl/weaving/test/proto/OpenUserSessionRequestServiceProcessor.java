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
package com.braintribe.model.processing.service.impl.weaving.test.proto;


import com.braintribe.model.processing.service.api.ServiceProcessorException;
import com.braintribe.model.processing.service.api.ServiceRequestContext;
import com.braintribe.model.processing.service.impl.weaving.test.SecurityServiceHandler;
import com.braintribe.model.processing.service.weaving.impl.dispatch.ServiceRequestHandler;
import com.braintribe.model.securityservice.OpenUserSession;
import com.braintribe.model.service.api.ServiceRequest;

public class OpenUserSessionRequestServiceProcessor implements ServiceRequestHandler {

	@Override
	public Object process(Object staticThis, ServiceRequestContext requestContext, ServiceRequest request) throws ServiceProcessorException {
		return ((SecurityServiceHandler)staticThis).openUserSession(requestContext, (OpenUserSession)request);
	}
}
