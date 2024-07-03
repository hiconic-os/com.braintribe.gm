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
