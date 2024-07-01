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
package com.braintribe.model.processing.session.api.notifying;

import com.braintribe.model.generic.GenericEntity;

public interface EntityCreation<T extends GenericEntity> {

	EntityCreation<T> raw();

	/**
	 * Specifies the new entity's partition.
	 * <p>
	 * NOTE that this value is set before manipulation tracking is on and the instantiation manipulation will be notified with this value already set.
	 */
	EntityCreation<T> withPartition(String partition);

	/** Equivalent to calling {@link #global(String)} with a random UUID as a parameter. */
	T globalWithRandomUuid();

	/**
	 * Creates a new entity with the specified globalId.
	 * <p>
	 * NOTE that the globalId value is set before manipulation tracking is on and the instantiation manipulation will be notified with this value
	 * already set.
	 */
	T global(String globalId);

	/**
	 * Creates a new entity with the specified id.
	 * <p>
	 * NOTE that the id value is set before manipulation tracking is on and the instantiation manipulation will be notified with this value already
	 * set.
	 */
	T persistent(Object id);

	T preliminary();

}
