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
package com.braintribe.model.processing.generic.synchronize;

import java.util.HashSet;
import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.processing.generic.synchronize.api.SynchronizationContext;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;

/**
 * Standard implementation of {@link SynchronizationContext} that keeps
 * according informations in member variables.
 */
public class BasicSynchronizationContext implements SynchronizationContext {

	private PersistenceGmSession session;
	private Set<GenericEntity> existingEntities = new HashSet<GenericEntity>();

	public BasicSynchronizationContext(PersistenceGmSession session) {
		this.session = session;
	}

	/**
	 * @see com.braintribe.model.processing.generic.synchronize.api.SynchronizationContext#foundInSession(com.braintribe.model.generic.GenericEntity)
	 */
	@Override
	public boolean foundInSession(GenericEntity instance) {
		return existingEntities.contains(instance);
	}

	/**
	 * @see com.braintribe.model.processing.generic.synchronize.api.SynchronizationContext#getSession()
	 */
	@Override
	public PersistenceGmSession getSession() {
		return this.session;
	};

	/**
	 * Register's passed entity as an entity that was found in session. All
	 * entities passed will be found afterwards via
	 * {@link #foundInSession(GenericEntity)}
	 */
	public void registerEntityFoundInSession(GenericEntity instance) {
		this.existingEntities.add(instance);
	}

}
