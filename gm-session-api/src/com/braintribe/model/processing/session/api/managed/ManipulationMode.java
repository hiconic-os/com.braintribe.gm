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
package com.braintribe.model.processing.session.api.managed;

import com.braintribe.model.generic.manipulation.EntityProperty;
import com.braintribe.model.generic.manipulation.LocalEntityProperty;
import com.braintribe.model.generic.value.EntityReference;
import com.braintribe.model.generic.value.GlobalEntityReference;
import com.braintribe.model.generic.value.PersistentEntityReference;
import com.braintribe.model.generic.value.PreliminaryEntityReference;

/**
 * Represents type of a manipulations in the manipulation stack.
 * 
 * @see ManipulationApplicationContext#getMode()
 */
public enum ManipulationMode {

	/**
	 * Manipulations that use {@link LocalEntityProperty} as it's owner and directly references GM entities.
	 */
	LOCAL,

	/**
	 * Manipulations that use {@link EntityProperty} as owner and values for properties are {@link EntityReference}s. In case an entity has not id a
	 * {@link PreliminaryEntityReference} is used, otherwise a {@link PersistentEntityReference} is used.
	 * <p>
	 * The <code>globalId</code> property is not treated in any special way in this mode.
	 */
	REMOTE,

	/**
	 * Similar to {@link #REMOTE} but uses {@link GlobalEntityReference} instead of {@link PersistentEntityReference} in case the entity has its
	 * globalId set.
	 * <p>
	 * The <code>id</code> property is not treated in any special way in this mode.
	 */
	REMOTE_GLOBAL,

}
