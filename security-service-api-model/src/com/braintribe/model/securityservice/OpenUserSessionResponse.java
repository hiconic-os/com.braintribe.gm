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

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.meta.Description;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.usersession.UserSession;

public interface OpenUserSessionResponse extends GenericEntity {

	EntityType<OpenUserSessionResponse> T = EntityTypes.T(OpenUserSessionResponse.class);

	UserSession getUserSession();
	void setUserSession(UserSession userSession);
	
	@Description("Marks if the session was already existing and could be acquired based on credentials (e.g basic auth, token should not create sessions all the time)")
	boolean getReused();
	void setReused(boolean reused);
}
