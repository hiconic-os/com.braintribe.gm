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
package com.braintribe.gm.security.test.auth.processing;

import java.util.UUID;

import com.braintribe.gm.security.test.auth.model.ServiceRequest1;
import com.braintribe.model.processing.service.api.ServiceProcessor;
import com.braintribe.model.processing.service.api.ServiceRequestContext;

public class ServiceProcessor1 implements ServiceProcessor<ServiceRequest1, String> {

	public static final String RETURN = UUID.randomUUID().toString();
	public static final Class<? extends Exception> EXCEPTION_TYPE = NegativeArraySizeException.class;
	
	@Override
	public String process(ServiceRequestContext context, ServiceRequest1 parameter) throws NegativeArraySizeException {
		if (parameter.getForceException()) {
			throw new NegativeArraySizeException("Enforced business exception for: " + parameter);
		}
		return RETURN;
	}

}