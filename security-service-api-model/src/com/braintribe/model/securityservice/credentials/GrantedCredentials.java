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
package com.braintribe.model.securityservice.credentials;


import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.securityservice.credentials.identification.UserIdentification;
import com.braintribe.model.securityservice.credentials.identification.UserNameIdentification;


public interface GrantedCredentials extends AbstractUserIdentificationCredentials {

	EntityType<GrantedCredentials> T = EntityTypes.T(GrantedCredentials.class);
	
	Credentials getGrantingCredentials();
	void setGrantingCredentials(Credentials grantingCredentials);
	
	static GrantedCredentials forUserAndGrantingSession(String userId, String grantingSessionId) {
		return of(UserNameIdentification.of(userId), ExistingSessionCredentials.of(grantingSessionId));
	}
	
	static GrantedCredentials of(UserIdentification userIdentification, Credentials grantingCredentials) {
		GrantedCredentials result = GrantedCredentials.T.create();
		result.setUserIdentification(userIdentification);
		result.setGrantingCredentials(grantingCredentials);
		
		return result;
	}
}
