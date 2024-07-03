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

import static java.util.Objects.requireNonNull;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.manipulation.DeleteMode;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.Property;

/**
 * @author peter.gazdik
 */
public interface EntityManager {

	/** Creates a raw instance of given type. Raw means the {@link Property#getInitializer() initial values} of properties are not set. */
	<T extends GenericEntity> T createRaw(EntityType<T> entityType);

	/** Returns entity with given {@code globalId}, or {@code null} if no such entity is found. */
	<T extends GenericEntity> T findEntityByGlobalId(String globalId);

	/** Returns entity with given {@code globalId}, or throws an exception if no such entity is found. */
	default <T extends GenericEntity> T getEntityByGlobalId(String globalId) {
		return requireNonNull(findEntityByGlobalId(globalId), () -> "No entity found with globalId: " + globalId);
	}

	void deleteEntity(GenericEntity entity, DeleteMode deleteMode);

}
