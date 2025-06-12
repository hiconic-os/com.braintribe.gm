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

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.securityservice.OpenUserSession;

@Abstract
public interface Credentials extends GenericEntity {

	EntityType<Credentials> T = EntityTypes.T(Credentials.class);

	/**
	 * Indicates that the credentials support acquiring of a session.
	 * <p>
	 * Acquiring a session means that {@link OpenUserSession} does not automatically create a new session, but it re-uses one that already exists for
	 * given credentials.
	 * <p>
	 * This is e.g. relevant for clients that send a token with each request (rather than a session id). The server then creates a new session on the
	 * first such request and re-uses it on next requests.
	 * 
	 * @see AcquirationSupportCredentials
	 */
	default boolean acquirationSupportive() {
		return false;
	}
}
