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
// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
// ============================================================================
package com.braintribe.model.processing.generic.synchronize.experts;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.meta.data.synchronization.ExternalId;
import com.braintribe.model.processing.generic.synchronize.api.SynchronizationContext;
import com.braintribe.model.processing.meta.cmd.builders.EntityMdResolver;
import com.braintribe.model.processing.session.api.managed.ModelAccessory;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;

/**
 * This implementation of {@link QueryingIdentityManager} inspects the given instance for properties with {@link ExternalId} MetaData <br />
 * and if available these properties will be used to do the identity management.
 */
public class ExternalIdIdentityManager extends ConfigurableIdentityManager {

	public ExternalIdIdentityManager() {}

	/**
	 * @see com.braintribe.model.processing.generic.synchronize.experts.ConfigurableIdentityManager#isResponsible(com.braintribe.model.generic.GenericEntity, com.braintribe.model.generic.reflection.EntityType, com.braintribe.model.processing.generic.synchronize.api.SynchronizationContext)
	 */
	@Override
	public boolean isResponsible(GenericEntity instance, EntityType<? extends GenericEntity> entityType, SynchronizationContext context) {
		if (responsibleFor != null) {
			// An explicit type is configured thus we let the super type decide.
			return super.isResponsible(instance, entityType, context);
		}
		// Check whether the current instance has an externalId property configured.
		return hasExternalId(context.getSession(), instance, entityType);
	}

	
	@Override
	public Collection<String> getIdentityProperties(GenericEntity instance,	EntityType<? extends GenericEntity> entityType, SynchronizationContext context) {
		return getExternalIdProperties(context.getSession(), instance, entityType);
	}
	
	private boolean hasExternalId(PersistenceGmSession session, GenericEntity instance, EntityType<? extends GenericEntity> entityType) {
		return (!getExternalIdProperties(session, instance, entityType).isEmpty());
	}

	private Set<String> getExternalIdProperties(PersistenceGmSession session, GenericEntity instance, EntityType<? extends GenericEntity> entityType) {
		ModelAccessory modelAccessory = session.getModelAccessory();
		if (modelAccessory == null) {
			// No model accessory available in session. Ignore.
			return Collections.emptySet();
		}
		
		EntityMdResolver entityMdResolver = modelAccessory.getMetaData().entity(instance);

		Set<String> externalIdProperties = new HashSet<String>();
		for (Property property : entityType.getProperties()) {
			if (entityMdResolver.property(property).is(ExternalId.T)) {
				externalIdProperties.add(property.getName());
			}
		}
		
		return externalIdProperties;
	}

}
