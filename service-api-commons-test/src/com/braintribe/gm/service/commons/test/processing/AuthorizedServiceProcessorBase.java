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

import org.junit.Assert;

import com.braintribe.exception.AuthorizationException;
import com.braintribe.gm.service.commons.test.model.AuthorizedServiceRequestBase;
import com.braintribe.model.processing.service.api.ServiceRequestContext;
import com.braintribe.model.service.api.AuthorizedRequest;
import com.braintribe.model.service.api.ServiceRequest;

public class AuthorizedServiceProcessorBase extends ServiceProcessorBase {
	
	protected static String extractSessionId(AuthorizedRequest serviceRequest) {

		Object sessionId = serviceRequest.getSessionId();

		if (sessionId == null && serviceRequest.getMetaData() != null) {
			sessionId = serviceRequest.getMetaData().get(AuthorizedRequest.sessionId);
		}

		if (sessionId == null) {
			String message = "No session id provided in call to authorization required request [ " + serviceRequest.entityType().getTypeSignature() + " ]";
			throw new AuthorizationException(message);
		}

		return sessionId.toString();

	}
	
	protected void validate(ServiceRequestContext context, ServiceRequest parameter) {
		if (parameter instanceof AuthorizedServiceRequestBase) {
			AuthorizedServiceRequestBase authorizedServiceRequestBase = (AuthorizedServiceRequestBase)parameter;
			if (authorizedServiceRequestBase.getValidateAuthorizationContext()) {
				Assert.assertTrue("A ServiceProcessor handling AuthorizedRequest should be called under an authorization context", context.isAuthorized());
				String sessionId = extractSessionId((AuthorizedRequest)parameter);
				Assert.assertEquals(sessionId, context.getRequestorSessionId());
			}
		}
	}

}