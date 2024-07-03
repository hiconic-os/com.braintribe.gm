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
package com.braintribe.model.processing.generic.synchronize.api;

import java.util.Collection;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.processing.generic.synchronize.GenericEntitySynchronizationException;
import com.braintribe.model.processing.generic.synchronize.api.builder.BasicIdentityManagerBuilders;
import com.braintribe.model.processing.generic.synchronize.api.builder.SynchronizationResultConvenience;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;

/**
 * The {@link GenericEntitySynchronization} can be used to synchronize (import)
 * given {@link GenericEntity} instances <br />
 * into a given session ({@link PersistenceGmSession}) by respecting provided or
 * pre-configured identity management strategies.
 */
public interface GenericEntitySynchronization {

	/**
	 * Adds the given entity to the collection of entities that should be
	 * synchronized.
	 */
	GenericEntitySynchronization addEntity(GenericEntity entity);

	/**
	 * Adds the given entities to the collection of entities that should be
	 * synchronized.
	 */
	GenericEntitySynchronization addEntities(Collection<? extends GenericEntity> entities);

	/**
	 * Can be called to clear all entities provided before. This is typically
	 * used when the same;@link AbstractSynchronization} <br />
	 * instance is used multiple times.
	 */
	GenericEntitySynchronization clearEntities();

	/**
	 * Sets the target {@link PersistenceGmSession} for the synchronization.
	 */
	GenericEntitySynchronization session(PersistenceGmSession session);

	/**
	 * Adds the given {@link IdentityManager} that should be used during
	 * synchronization.
	 */
	GenericEntitySynchronization addIdentityManager(IdentityManager identityManager);

	/**
	 * Adds the given {@link IdentityManager}'s that should be used during
	 * synchronization.
	 */
	GenericEntitySynchronization addIdentityManagers(Collection<IdentityManager> identityManagers);

	/**
	 * Adds the default identity managers.
	 */
	GenericEntitySynchronization addDefaultIdentityManagers();

	/**
	 * Provides a builder to create and add a new;@link IdentityManager}
	 * fluently.
	 */
	BasicIdentityManagerBuilders<? extends GenericEntitySynchronization> addIdentityManager();

	/**
	 * Tells the synchronization to include id properties during
	 * synchronization.
	 */
	GenericEntitySynchronization includeIdProperties();

	/**
	 * Tells the synchronization to commit the session after successful
	 * synchronization.
	 */
	GenericEntitySynchronization commitAfterSynchronization();

	/**
	 * Synchronizes the given entities based on the given identity management
	 * strategies. <br />
	 * Returns a collection of synchronized (session bound) entities that represents
	 * the initial added entities.
	 */
	SynchronizationResultConvenience synchronize() throws GenericEntitySynchronizationException;

}
