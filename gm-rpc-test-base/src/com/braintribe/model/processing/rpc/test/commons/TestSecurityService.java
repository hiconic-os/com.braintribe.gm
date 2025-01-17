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
package com.braintribe.model.processing.rpc.test.commons;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.Reasons;
import com.braintribe.gm.model.security.reason.InvalidCredentials;
import com.braintribe.gm.model.security.reason.SessionNotFound;
import com.braintribe.model.processing.securityservice.api.exceptions.AuthenticationException;
import com.braintribe.model.processing.service.api.ServiceRequestContext;
import com.braintribe.model.processing.service.impl.AbstractDispatchingServiceProcessor;
import com.braintribe.model.processing.service.impl.DispatchConfiguration;
import com.braintribe.model.securityservice.Logout;
import com.braintribe.model.securityservice.LogoutSession;
import com.braintribe.model.securityservice.OpenUserSession;
import com.braintribe.model.securityservice.OpenUserSessionResponse;
import com.braintribe.model.securityservice.SecurityRequest;
import com.braintribe.model.securityservice.ValidateUserSession;
import com.braintribe.model.securityservice.credentials.Credentials;
import com.braintribe.model.securityservice.credentials.ExistingSessionCredentials;
import com.braintribe.model.user.User;
import com.braintribe.model.usersession.UserSession;
import com.braintribe.model.usersession.UserSessionType;

public class TestSecurityService extends AbstractDispatchingServiceProcessor<SecurityRequest, Object> {

	private final Map<String, UserSession> userSessions = new ConcurrentHashMap<String, UserSession>();
	private final User testUser = createTestUser();

	public TestSecurityService() {

		UserSession userSession = UserSession.T.create();
		userSession.setCreationDate(new Date());
		userSession.setSessionId("valid-session-id");
		userSession.setType(UserSessionType.normal);
		userSession.setUser(testUser);

		userSessions.put(userSession.getSessionId(), userSession);

	}

	@Override
	protected void configureDispatching(DispatchConfiguration<SecurityRequest, Object> dispatching) {
		dispatching.registerReasoned(OpenUserSession.T, (c, r) -> openUserSession(r));
		dispatching.register(Logout.T, this::logout);
		dispatching.register(LogoutSession.T, this::logoutSession);
		dispatching.registerReasoned(ValidateUserSession.T, this::validateUserSession);
	}

	private Maybe<OpenUserSessionResponse> openUserSession(OpenUserSession request) throws AuthenticationException {

		Credentials credentials = request.getCredentials();

		UserSession userSession = null;

		if (credentials instanceof ExistingSessionCredentials) {

			String existingSessionId = ((ExistingSessionCredentials) credentials).getExistingSessionId();

			userSession = userSessions.get(existingSessionId);

			if (userSession == null) {
				String message = "Session id is invalid: " + existingSessionId;
				return Reasons.build(InvalidCredentials.T).text(message).toMaybe();
			}

		} else {

			userSession = UserSession.T.create();
			userSession.setCreationDate(new Date());
			userSession.setSessionId(UUID.randomUUID().toString());
			userSession.setType(UserSessionType.normal);
			userSession.setUser(testUser);

			userSessions.put(userSession.getSessionId(), userSession);

		}

		OpenUserSessionResponse response = OpenUserSessionResponse.T.create();
		response.setUserSession(userSession);

		return Maybe.complete(response);
	}

	private boolean logout(ServiceRequestContext context, Logout request) {
		String sessionId = context.getRequestorSessionId();
		if (sessionId == null)
			return false;

		boolean loggedOut = userSessions.remove(sessionId) != null;
		return loggedOut;
	}

	private boolean logoutSession(ServiceRequestContext context, LogoutSession request) {
		String sessionId = request.getSessionId();
		if (sessionId == null)
			return false;

		boolean loggedOut = userSessions.remove(sessionId) != null;
		return loggedOut;
	}

	private Maybe<UserSession> validateUserSession(ServiceRequestContext requestContext, ValidateUserSession request) {

		UserSession userSession = userSessions.get(request.getSessionId());

		if (userSession == null) {
			return Reasons.build(SessionNotFound.T).text("Session not found: " + request.getSessionId()).toMaybe();
		}

		return Maybe.complete(userSession);

	}

	private static User createTestUser() {
		User user = User.T.create();
		user.setId(UUID.randomUUID().toString());
		user.setName("test.user");
		user.setFirstName("Test");
		user.setLastName("User");
		return user;
	}

}
