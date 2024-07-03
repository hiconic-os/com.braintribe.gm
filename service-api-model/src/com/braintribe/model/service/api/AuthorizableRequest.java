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
package com.braintribe.model.service.api;

import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * The AuthorizableRequest can be used as super type if some request optionally supports a session being given from the caller in order to verify its priviledges.
 * @author Dirk Scheffler
 *
 */
@Abstract
public interface AuthorizableRequest extends ServiceRequest {

	EntityType<AuthorizableRequest> T = EntityTypes.T(AuthorizableRequest.class);

	String sessionId = "sessionId";

	/**
	 * The id of a session that should be used to check priviledges/roles in the processing
	 */
	String getSessionId();
	void setSessionId(String sessionId);

	@Override
	default boolean supportsAuthentication() {
		return true;
	}
	
	@Override
	default boolean requiresAuthentication() {
		return getSessionId() != null;
	}
}
