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

import com.braintribe.gm.service.commons.test.model.ServiceRequest5;
import com.braintribe.model.processing.service.api.ServiceProcessor;
import com.braintribe.model.processing.service.api.ServiceProcessorException;
import com.braintribe.model.processing.service.api.ServiceRequestContext;

public class ServiceProcessor5 extends AuthorizedServiceProcessorBase implements ServiceProcessor<ServiceRequest5, String> {

	public String identification;

	public ServiceProcessor5() {
	}

	public ServiceProcessor5(String identification) {
		this.identification = identification+"/service";
	}

	@Override
	public String process(ServiceRequestContext requestContext, ServiceRequest5 request) throws ServiceProcessorException {
		if (identification != null) {
			if (identification.equals(request.getServiceId())) {
				return "deployed-"+request.getServiceId();
			} else {
				throw new ServiceProcessorException("Incoming identification \"" + request.getServiceId() + "\" differs from expected " + identification);
			}
		} else {
			return request.getServiceId();
		}
	}

}