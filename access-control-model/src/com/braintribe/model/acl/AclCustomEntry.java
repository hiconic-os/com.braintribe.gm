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
package com.braintribe.model.acl;

import com.braintribe.model.generic.annotation.meta.Mandatory;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * {@link AclEntry} that allows a custom String value as it's {@link #getOperation() operation}.
 * 
 * @see AclEntry
 * @see AclCustomOperationEntry
 * 
 * @author Dirk Scheffler
 */
public interface AclCustomEntry extends AclEntry {
	EntityType<AclCustomEntry> T = EntityTypes.T(AclCustomEntry.class);

	/**
	 * Mandatory string property to satisfy the {@link AclEntry#operation()} method
	 */
	@Mandatory
	String getOperation();
	void setOperation(String access);

	/**
	 * @see AclEntry#operation()
	 */
	@Override
	default String operation() {
		return getOperation();
	}
}
