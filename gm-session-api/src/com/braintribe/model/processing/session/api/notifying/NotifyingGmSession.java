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
package com.braintribe.model.processing.session.api.notifying;

import java.util.Stack;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.manipulation.ManifestationManipulation;
import com.braintribe.model.generic.manipulation.Manipulation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.PropertyAccessInterceptor;
import com.braintribe.model.generic.session.GmSession;
import com.braintribe.model.generic.session.exception.GmSessionRuntimeException;
import com.braintribe.model.generic.tracking.ManipulationListener;
import com.braintribe.model.processing.session.api.managed.ManagedGmSession;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;

/**
 * An extension of {@link GmSession} that serves as hub for noticing {@link Manipulation} events. Using the
 * {@link GenericManipulationListenerRegistry} it is possible to add {@link ManipulationListener}s for all the session manipulations, or filtered just
 * for given entity, or just a property of given entity.
 * <p>
 * This session also provides a more advanced control of {@link PropertyAccessInterceptor}s (compared to just {@linkplain GmSession}). With
 * {@link PropertyAccessInterceptorRegistry} accessible via {@link #interceptors()} method, it is possible to add/remove such interceptors, and also
 * control the relative order of interceptors.
 * 
 * <h3>Querying attached entities</h3>
 * 
 * This session does not provide the functionality to query entities attached to it.
 * 
 * @see GmSession
 * @see ManagedGmSession
 * @see PersistenceGmSession
 * @see GenericManipulationListenerRegistry
 * @see PropertyAccessInterceptorRegistry
 */
public interface NotifyingGmSession extends GmSession {

	/** Builder with more creation options, compared to the create methods (e.g. {@link #create(EntityType)}) */
	<T extends GenericEntity> EntityCreation<T> createEntity(EntityType<T> type);

	/**
	 * {@inheritDoc}
	 * 
	 * This method also creates a {@link ManifestationManipulation} which is used to notify all the listeners.
	 */
	@Override
	void attach(GenericEntity entity) throws GmSessionRuntimeException;

	GenericManipulationListenerRegistry listeners();

	PropertyAccessInterceptorRegistry interceptors();

	Stack<CompoundNotification> getCompoundNotificationStack();

}
