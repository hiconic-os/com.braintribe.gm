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

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.annotation.Initializer;
import com.braintribe.model.generic.annotation.meta.Mandatory;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * A single piece of configuration which specifies a grant or deny {@link #getPermission() permission} for a {@link #getRole() role} to perform given
 * {@link #operation() operation}.
 * <p>
 * Note that for the ACL to work properly, user should not create it's own custom sub-types, but extend either {@link AclCustomEntry} or
 * {@link AclCustomOperationEntry}!
 * 
 * @see Acl
 * @see AclCustomEntry
 * @see AclStandardEntry
 * @see AclCustomOperationEntry
 * 
 * @author Dirk Scheffler
 */
@Abstract
public interface AclEntry extends GenericEntity {

	EntityType<AclEntry> T = EntityTypes.T(AclEntry.class);

	/**
	 * Specifies the permission of this entry.
	 * <p>
	 * Note that {@link AclPermission#DENY DENY} has higher priority than {@link AclPermission#GRANT GRANT}, i.e. if two entries with same values but
	 * opposite permissions are defined, DENY is resolved.
	 */
	@Initializer("enum(com.braintribe.model.acl.AclPermission,GRANT)")
	@Mandatory
	AclPermission getPermission();
	void setPermission(AclPermission permission);

	/** The role who's permissions are configured by this entry. */
	@Mandatory
	String getRole();
	void setRole(String role);

	/**
	 * The operation in form of a functional method that is implemented with the default methods {@link AclStandardEntry#operation()},
	 * {@link AclCustomEntry#operation()} and {@link AclCustomOperationEntry#operation()}.
	 */
	default String operation() {
		throw new UnsupportedOperationException(
				"This code should not have been reached. This method is implemented in " + AclStandardEntry.class.getSimpleName() + ", "
						+ AclCustomEntry.class.getSimpleName() + " and " + AclCustomOperationEntry.class.getSimpleName()
						+ ". Every ACL entry must be a sub-type of one of those, and must never implement this method itself.");
	}

}
