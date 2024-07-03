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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.braintribe.logging.Logger;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.processing.generic.synchronize.api.IdentityManager;
import com.braintribe.model.processing.generic.synchronize.api.SynchronizationContext;

/**
 * Registry that holds {@link IdentityManager}'s and identifies responsible
 * managers for passed entities.
 */
public class IdentityManagerRegistry {

	private static Logger logger = Logger.getLogger(IdentityManagerRegistry.class);

	private List<IdentityManager> identityManagers = new ArrayList<IdentityManager>();

	/**
	 * Default constructor
	 */
	public IdentityManagerRegistry() {}

	/**
	 * Adds a new {@link IdentityManager} to the registry.
	 */
	public void addIdentityManager(IdentityManager identityManager) {
		this.identityManagers.add(identityManager);
	}

	/**
	 * Same as {@link #addIdentityManager(IdentityManager)} but for multiple
	 * managers.
	 */
	public void addIdentityManagers(Collection<IdentityManager> identityManagers) {
		this.identityManagers.addAll(identityManagers);
	}

	/**
	 * Tries to find the first responsible {@link IdentityManager} for given
	 * entity. <br />
	 * Returns null in case no registered {@link IdentityManager} returns
	 * responsibility for given entity.
	 */
	public IdentityManager findIdentityManager(GenericEntity instance, EntityType<? extends GenericEntity> entityType,
			SynchronizationContext context) {

		for (IdentityManager identityManager : identityManagers) {
			if (identityManager.isResponsible(instance, entityType, context)) {
				return identityManager;
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("No responsible identity manager found for entity type: " + entityType.getTypeSignature());
		}
		return null;
	}

}
