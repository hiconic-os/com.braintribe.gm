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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.braintribe.common.lcd.ConfigurationException;
import com.braintribe.common.lcd.GenericRuntimeException;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.session.exception.GmSessionException;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.query.EntityQuery;

/**
 * This {@link EntitiesFinder} is used to find entities based on a configured {@link EntityQuery}. For more info see
 * method {@link #findEntities(PersistenceGmSession)}.
 *
 */
public class EntityQueryBasedEntitiesFinder implements EntitiesFinder {

	private EntityQuery entityQuery;

	/**
	 * Executes an {@link EntityQuery} and returns the respective entities.
	 *
	 * @throws ConfigurationException
	 *             if the entity query is <code>null</code>.
	 */
	@Override
	public Set<GenericEntity> findEntities(final PersistenceGmSession session) throws ConfigurationException, GenericRuntimeException {

		if (this.entityQuery == null) {
			throw new ConfigurationException("The query configured must not be null!");
		}

		Set<GenericEntity> foundEntities = null;

		try {
			List<GenericEntity> entities = session.query().entities(this.entityQuery).list();
			foundEntities = new HashSet<GenericEntity>(entities);
		} catch (final GmSessionException e) {
			throw new GenericRuntimeException("Error while finding entities: query execution failed!", e);
		}

		return foundEntities;
	}

	public EntityQuery getEntityQuery() {
		return this.entityQuery;
	}

	public void setEntityQuery(final EntityQuery entityQuery) {
		this.entityQuery = entityQuery;
	}
}
