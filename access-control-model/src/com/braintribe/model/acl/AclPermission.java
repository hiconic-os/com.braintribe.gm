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

import com.braintribe.model.generic.base.EnumBase;
import com.braintribe.model.generic.reflection.EnumType;
import com.braintribe.model.generic.reflection.EnumTypes;

/**
 * This enum is used to determine if a {@link AclCustomEntry} grants access or denies access.
 * @author Dirk Scheffler
 *
 */
public enum AclPermission implements EnumBase {
	GRANT, DENY;

	public static final EnumType<AclPermission> T = EnumTypes.T(AclPermission.class);
	
	@Override
	public EnumType<AclPermission> type() {
		return T;
	}	
}
