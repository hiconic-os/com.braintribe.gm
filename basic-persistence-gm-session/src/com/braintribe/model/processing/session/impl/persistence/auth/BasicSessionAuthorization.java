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
package com.braintribe.model.processing.session.impl.persistence.auth;

import java.util.Set;

import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.Required;
import com.braintribe.model.processing.session.api.persistence.auth.SessionAuthorization;

public class BasicSessionAuthorization implements SessionAuthorization {

	private String userId;
	private String userName;
	private String sessionId;
	private Set<String> userRoles;
	
	@Required @Configurable
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	@Required @Configurable
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	@Required @Configurable
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
	@Required @Configurable
	public void setUserRoles(Set<String> userRoles) {
		this.userRoles = userRoles;
	}
	
	@Override
	public String getUserId() {
		return userId;
	}

	@Override
	public String getUserName() {
		return userName;
	}
	
	@Override
	public String getSessionId() {
		return sessionId;
	}

	@Override
	public Set<String> getUserRoles() {
		return userRoles;
	}

}
