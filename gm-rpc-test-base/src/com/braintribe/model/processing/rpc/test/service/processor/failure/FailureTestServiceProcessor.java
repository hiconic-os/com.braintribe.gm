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
package com.braintribe.model.processing.rpc.test.service.processor.failure;

import com.braintribe.common.ExceptionBuilder;
import com.braintribe.model.processing.service.api.ServiceProcessor;
import com.braintribe.model.processing.service.api.ServiceProcessorException;
import com.braintribe.model.processing.service.api.ServiceRequestContext;

public class FailureTestServiceProcessor implements ServiceProcessor<FailureTestServiceProcessorRequest, Boolean> {

	@Override
	public Boolean process(ServiceRequestContext requestContext, FailureTestServiceProcessorRequest request)
			throws ServiceProcessorException {

		if (request.getExceptionType() == null) {
			throw new ServiceProcessorException("SPE with no cause");
		}

		Throwable createException = null;
		try {
			createException = ExceptionBuilder.createException(request.getExceptionType(),
					"Implementation thrown " + request.getExceptionType());

		} catch (Exception e) {
			throw new ServiceProcessorException("Failed to build " + request.getExceptionType(), e);
		}

		if (createException == null) {
			throw new ServiceProcessorException("null instead of " + request.getExceptionType());
		}

		if (createException instanceof RuntimeException) {
			throw (RuntimeException) createException;
		}

		if (createException instanceof Error) {
			throw (Error) createException;
		}

		throw new ServiceProcessorException(createException.getMessage(), createException);

	}

}
