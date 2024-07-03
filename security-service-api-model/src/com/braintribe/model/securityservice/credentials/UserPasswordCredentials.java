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

import com.braintribe.model.generic.annotation.Initializer;
import com.braintribe.model.generic.annotation.ToStringInformation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.securityservice.credentials.identification.UserNameIdentification;

@ToStringInformation("UserPasswordCredentials[user=${userIdentification},password=****]")
public interface UserPasswordCredentials extends AbstractUserIdentificationCredentials, HasPassword, AcquirationSupportCredentials {

	EntityType<UserPasswordCredentials> T = EntityTypes.T(UserPasswordCredentials.class);

	static UserPasswordCredentials forUserName(String userName, String password) {
		UserPasswordCredentials result = UserPasswordCredentials.T.create();
		result.setUserIdentification(UserNameIdentification.of(userName));
		result.setPassword(password);

		return result;
	}

	@Initializer("false")
	boolean getPasswordIsEncrypted();
	void setPasswordIsEncrypted(boolean passwordIsEncrypted);
}
