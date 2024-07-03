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

import java.util.Set;
import java.util.function.Supplier;

import com.braintribe.logging.Logger;
import com.braintribe.model.usersession.UserSession;


public class RolesFromUserSessionProvider extends UserSessionProviderDelegatingProvider<UserSession> implements Supplier<Set<String>> {

	private static Logger log = Logger.getLogger(RolesFromUserSessionProvider.class);

	@Override
	public Set<String> get() throws RuntimeException {
		UserSession userSession = provideUserSession(log);
		return (userSession != null) ? userSession.getEffectiveRoles() : null;
	}

}
