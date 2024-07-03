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
 * StandardRequest is a convenience aggregator type that can be used in many cases as the super type for requests as it is a {@link DispatchableRequest} and a {@link AuthorizedRequest}.
 * @author Dirk Scheffler
 *
 */
@Abstract
public interface StandardRequest extends DispatchableRequest, AuthorizedRequest {
	
	EntityType<StandardRequest> T = EntityTypes.T(StandardRequest.class);

	@Override
	default  boolean dispatchable() {
		return true;
	}

	@Override
	default  boolean requiresAuthentication() {
		return true;
	}

}
