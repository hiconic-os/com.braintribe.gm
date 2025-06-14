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

import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * Base type for {@link Credentials} where "session acquiring" can optionally be turned on by setting {@link #getAcquire() acquire} property to
 * <code>true</code>.
 * 
 * @see Credentials#acquirationSupportive()
 */
@Abstract
public interface AcquirationSupportCredentials extends Credentials {

	EntityType<AcquirationSupportCredentials> T = EntityTypes.T(AcquirationSupportCredentials.class);

	Boolean getAcquire();
	void setAcquire(Boolean acquire);

	/** {@inheritDoc} */
	@Override
	default boolean acquirationSupportive() {
		Boolean acquire = getAcquire();
		return acquire != null ? acquire.booleanValue() : false;
	}
}
