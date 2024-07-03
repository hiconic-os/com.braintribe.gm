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
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.utils.lcd.CommonTools;

/**
 * This {@link EntitiesFinder} is used to combine the result of {@link #setDelegates(List) multiple EntitiesFinders}.
 * For more info see method {@link #findEntities(PersistenceGmSession)}.
 *
 */
public class CompoundEntitiesFinder implements EntitiesFinder {

	private List<EntitiesFinder> delegates;

	/**
	 * Passes the request to the configured {@link #setDelegates(List) delegates} and returns the union of the result
	 * sets.
	 *
	 * @throws ConfigurationException
	 *             if the list of delegates is <code>null</code>.
	 */
	@Override
	public Set<GenericEntity> findEntities(final PersistenceGmSession session) throws ConfigurationException {

		if (this.delegates == null) {
			throw new ConfigurationException("The configured list of delegates must not be null!");
		}

		final Set<GenericEntity> foundEntitiesUnion = new HashSet<GenericEntity>();

		for (final EntitiesFinder entitiesFinder : this.delegates) {
			final Set<GenericEntity> foundEntities = entitiesFinder.findEntities(session);
			if (!CommonTools.isEmpty(foundEntities)) {
				foundEntitiesUnion.addAll(foundEntities);
			}
		}

		return foundEntitiesUnion;
	}

	public List<EntitiesFinder> getDelegates() {
		return this.delegates;
	}

	public void setDelegates(final List<EntitiesFinder> delegates) {
		this.delegates = delegates;
	}
}
