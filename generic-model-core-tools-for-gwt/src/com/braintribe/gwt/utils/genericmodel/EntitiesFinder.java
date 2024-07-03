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
package com.braintribe.gwt.utils.genericmodel;

import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;

/**
 * {@link #findEntities(PersistenceGmSession) Finds} a set of (relevant) entities.
 *
 * @author michael.lafite
 */
public interface EntitiesFinder {

	/**
	 * Finds a set of (relevant) entities. Which entities are relevant depends on the actual
	 * implementation/configuration.
	 *
	 * @param session
	 *            the session the entities will be searched with and loaded from.
	 * @return the set of entities.
	 */
	Set<GenericEntity> findEntities(PersistenceGmSession session);

}
