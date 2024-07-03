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
package com.braintribe.model.processing.service.common;

import java.util.Objects;

import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.Required;
import com.braintribe.logging.ThreadRenamer;
import com.braintribe.model.processing.service.api.ProceedContext;
import com.braintribe.model.processing.service.api.ServiceAroundProcessor;
import com.braintribe.model.processing.service.api.ServiceRequestContext;
import com.braintribe.model.service.api.ServiceRequest;

public class ThreadNamingInterceptor implements ServiceAroundProcessor<ServiceRequest, Object> {
	private ThreadRenamer threadRenamer = ThreadRenamer.NO_OP;

	@Required
	@Configurable
	public void setThreadRenamer(ThreadRenamer threadRenamer) {
		Objects.requireNonNull(threadRenamer, "threadRenamer cannot be set to null");
		this.threadRenamer = threadRenamer;
	}

	@Override
	public Object process(ServiceRequestContext context, ServiceRequest request, ProceedContext proceedContext) {
		threadRenamer.push(() -> "eval(" + threadNamePart(request) + ")");

		try {
			Object result = proceedContext.proceed(request);
			return result;

		} finally {
			threadRenamer.pop();
		}
	}
	
	private static String threadNamePart(ServiceRequest request) {
		if (request == null) {
			return "null";
		}
		return request.entityType().getShortName();
	}

}
