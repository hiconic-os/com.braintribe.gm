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
package com.braintribe.model.processing.securityservice.commons.service;

import com.braintribe.model.processing.service.api.ServiceProcessor;
import com.braintribe.model.processing.service.api.ServiceRequestContext;
import com.braintribe.model.service.api.ExecuteAuthorized;
import com.braintribe.model.service.api.ServiceRequest;

public class ExecuteAuthorizedServiceProcessor implements ServiceProcessor<ExecuteAuthorized, Object> {

	public final static ExecuteAuthorizedServiceProcessor INSTANCE = new ExecuteAuthorizedServiceProcessor();

	private ExecuteAuthorizedServiceProcessor() {
		// do nothing
	}

	@Override
	public Object process(ServiceRequestContext requestContext, ExecuteAuthorized request) {
		ServiceRequest serviceRequest = request.getServiceRequest();
		Object result = serviceRequest.eval(requestContext).get();
		return result;
	}
}