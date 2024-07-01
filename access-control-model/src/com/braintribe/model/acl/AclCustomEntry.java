// ============================================================================
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
// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
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
