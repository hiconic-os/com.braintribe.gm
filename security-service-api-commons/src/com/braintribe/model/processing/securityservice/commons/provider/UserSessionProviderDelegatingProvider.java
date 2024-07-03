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
package com.braintribe.model.processing.securityservice.commons.provider;

import java.util.function.Supplier;

import com.braintribe.cfg.Required;
import com.braintribe.logging.Logger;
import com.braintribe.model.usersession.UserSession;


/**
 * Abstraction for {@link Supplier}(s) delegating to a {@code Provider<UserSession>}.
 * 
 */
public abstract class UserSessionProviderDelegatingProvider<T extends UserSession> {

	private static Logger log = Logger.getLogger(UserSessionProviderDelegatingProvider.class);

	protected Supplier<T> userSessionProvider;

	@Required
	public void setUserSessionProvider(Supplier<T> userSessionProvider) {
		this.userSessionProvider = userSessionProvider;
	}

	/**
	 * See {@link #provideUserSession(Logger)}
	 */
	protected T provideUserSession() throws RuntimeException {
		return provideUserSession(log);
	}

	/**
	 * <p>
	 * Provides a {@link UserSession} via {@link UserSessionProviderDelegatingProvider#userSessionProvider}.
	 * 
	 * <p>
	 * Meant to reduce redundant logging in {@link UserSessionProviderDelegatingProvider} sub classes, which still could access {@link #userSessionProvider} directly if needed.
	 * 
	 * @return The {@link UserSession} provided by {@link UserSessionProviderDelegatingProvider#userSessionProvider}.
	 */
	protected T provideUserSession(Logger callerLogger) throws RuntimeException {
		T userSession = userSessionProvider.get();
		if (callerLogger.isTraceEnabled()) {
			callerLogger.trace("Provider ["+userSessionProvider+"] provided user session ["+userSession+"]");
		}
		return userSession;
	}

}
