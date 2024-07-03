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
 * A DomainRequest is used to make the service domain who's model is used to associate processors and interceptors configurable per request.
 */
@Abstract
public interface DomainRequest extends ServiceRequest {

	EntityType<DomainRequest> T = EntityTypes.T(DomainRequest.class);

	String domainId = "domainId";

	/**
	 * The id of the service domain which has to be used to evaluate the request.
	 */
	String getDomainId();
	void setDomainId(String domainId);

	@Override
	default String domainId() {
		String d = getDomainId();
		if (d == null) {
			// Enables subtypes to infer the default domainId() value from their initializers
			return (String) this.entityType().getProperty(domainId).getInitializer();
		} else {
			return d;
		}
	}

}
