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
 * Request types derived from  DispatchableRequest allow to choose an individual processor per request using the serviceId property to select it. 
 * The DispatchableRequest can be compared with an instance bound method in an object oriented programming language while a non dispatchable request
 * can be compared with a static method.
 * 
 * @author Dirk Scheffler
 *
 */
@Abstract
public interface DispatchableRequest extends ServiceRequest {

	EntityType<DispatchableRequest> T = EntityTypes.T(DispatchableRequest.class);

	/**
	 * The optional id of the processor that is to be used to process the request.
	 */
	String getServiceId();
	void setServiceId(String serviceId);

	@Override
	default boolean dispatchable() {
		return true;
	}

}
