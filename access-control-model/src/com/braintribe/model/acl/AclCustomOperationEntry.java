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

import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.Property;

/**
 * Base for an {@link AclEntry} with a custom operation type.
 * <p>
 * The basic ACL entry sub-types are {@link AclStandardEntry}, which has {@link AclOperation} as it's operation type, which defines all it's possible
 * operations. Then there is {@link AclCustomOperationEntry}, which is modeled to be fully flexible, with it's operation property being of type
 * String, but that lacks the convenience of being able to select from a set of known values.
 * <p>
 * This entry is meant as mixture in between, where the sub-type is supposed to have a property called "operation" of any type it wants (most probably
 * a custom enum) and this abstract type ensures that the Acl mechanism uses the {@link Object#toString() toString} value of that operation.
 * <p>
 * NOTE that 
 * 
 * @see AclEntry
 * 
 * @author Dirk Scheffler
 */
@Abstract
public interface AclCustomOperationEntry extends AclEntry {
	EntityType<AclCustomOperationEntry> T = EntityTypes.T(AclCustomOperationEntry.class);

	/**
	 * @see AclEntry#operation()
	 */
	@Override
	default String operation() {
		Property property = entityType().getProperty("operation");
		Object propertyValue = property.get(this);
		return propertyValue == null ? null : propertyValue.toString();
	}
}
