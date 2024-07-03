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
package com.braintribe.model.securityservice;

import java.util.Date;
import java.util.Map;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.user.User;

public interface AuthenticatedUser extends AuthenticateCredentialsResponse {

	EntityType<AuthenticatedUser> T = EntityTypes.T(AuthenticatedUser.class);

	String user = "user";
	String properties = "properties";

	User getUser();
	void setUser(User user);

	Map<String, String> getProperties();
	void setProperties(Map<String, String> properties);

	Date getExpiryDate();
	void setExpiryDate(Date expiryDate);

	boolean getEnsureUserPersistence();
	void setEnsureUserPersistence(boolean ensureUserPersistence);

	boolean getInvalidateCredentialsOnLogout();
	void setInvalidateCredentialsOnLogout(boolean invalidateCredentialsOnLogout);
}
