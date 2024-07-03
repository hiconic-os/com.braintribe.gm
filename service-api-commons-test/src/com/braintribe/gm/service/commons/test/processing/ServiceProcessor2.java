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
package com.braintribe.gm.service.commons.test.processing;

import com.braintribe.gm.service.commons.test.model.ServiceRequest2;
import com.braintribe.model.processing.service.api.ServiceProcessor;
import com.braintribe.model.processing.service.api.ServiceRequestContext;

public class ServiceProcessor2 extends AuthorizedServiceProcessorBase implements ServiceProcessor<ServiceRequest2, Long> {
	
	public static final Long RETURN = System.currentTimeMillis();
	public static final Class<? extends Exception> EXCEPTION_TYPE = IndexOutOfBoundsException.class;

	@Override
	public Long process(ServiceRequestContext context, ServiceRequest2 parameter) throws IndexOutOfBoundsException {
		if (parameter.getForceException()) {
			throw new IndexOutOfBoundsException("Enforced business exception for: " + parameter);
		}
		validate(context, parameter);
		return RETURN;
	}

}