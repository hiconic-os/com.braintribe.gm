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
package com.braintribe.model.processing.securityservice.impl;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.Reasons;
import com.braintribe.gm.model.reason.essential.InvalidArgument;
import com.braintribe.model.processing.service.api.ReasonedServiceProcessor;
import com.braintribe.model.processing.service.api.ServiceRequestContext;
import com.braintribe.model.securityservice.AuthenticateCredentials;
import com.braintribe.model.securityservice.AuthenticateCredentialsResponse;
import com.braintribe.model.securityservice.AuthenticatedUser;
import com.braintribe.model.securityservice.AuthenticatedUserSession;
import com.braintribe.model.securityservice.credentials.Credentials;
import com.braintribe.model.user.User;
import com.braintribe.model.usersession.UserSession;

public abstract class AbstractAuthenticateCredentialsServiceProcessor<T extends Credentials>
		implements ReasonedServiceProcessor<AuthenticateCredentials, AuthenticateCredentialsResponse> {
	@Override
	public Maybe<? extends AuthenticateCredentialsResponse> processReasoned(ServiceRequestContext context, AuthenticateCredentials request) {
		T credentials = (T) request.getCredentials();

		if (credentials == null)
			return Reasons.build(InvalidArgument.T).text("AuthenticateCredentials.credentials must not be null").toMaybe();

		return authenticateCredentials(context, request, credentials);
	}

	protected abstract Maybe<AuthenticateCredentialsResponse> authenticateCredentials(ServiceRequestContext context, AuthenticateCredentials request,
			T credentials);

	protected AuthenticatedUser buildAuthenticatedUserFrom(User user) {
		AuthenticatedUser authenticatedUser = AuthenticatedUser.T.create();
		authenticatedUser.setUser(user);

		return authenticatedUser;
	}

	protected AuthenticatedUserSession buildAuthenticatedUserSessionFrom(UserSession userSession) {
		AuthenticatedUserSession authenticatedUserSession = AuthenticatedUserSession.T.create();
		authenticatedUserSession.setUserSession(userSession);

		return authenticatedUserSession;
	}
}
