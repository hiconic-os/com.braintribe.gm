// ============================================================================
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
// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
// ============================================================================
package com.braintribe.model.processing.securityservice.commons.provider;

import java.util.Set;
import java.util.function.Supplier;

import com.braintribe.cfg.Configurable;
import com.braintribe.logging.Logger;
import com.braintribe.model.processing.session.api.persistence.auth.SessionAuthorization;
import com.braintribe.model.usersession.UserSession;

public class SessionAuthorizationFromUserSessionProvider extends UserSessionProviderDelegatingProvider<UserSession> implements Supplier<SessionAuthorization> {

	private static Logger log = Logger.getLogger(SessionAuthorizationFromUserSessionProvider.class);
	
	private Supplier<SessionAuthorization> defaultSessionAuthorizationProvider;
	
	@Configurable
	public void setDefaultSessionAuthorizationProvider(
			Supplier<SessionAuthorization> defaultSessionAuthorizationProvider) {
		this.defaultSessionAuthorizationProvider = defaultSessionAuthorizationProvider;
	}
	
	
	@Override
	public SessionAuthorization get() throws RuntimeException {
		final UserSession userSession = provideUserSession(log);
		if (userSession == null) {
			if (defaultSessionAuthorizationProvider != null) {
				return defaultSessionAuthorizationProvider.get();	
			}
			return null;
		}
		
		return new SessionAuthorization() {
			@Override
			public Set<String> getUserRoles() {
				return userSession.getEffectiveRoles();
			}
			
			@Override
			public String getUserId() {
				return userSession.getUser().getId();
			}
			
			@Override
			public String getUserName() {
				return userSession.getUser().getName();
			}

			@Override
			public String getSessionId() {
				return userSession.getSessionId();
			}
		};
	}

}
