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
package com.braintribe.model.processing.generic.synchronize.api;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.processing.generic.synchronize.EntityNotFoundInSessionException;
import com.braintribe.model.processing.generic.synchronize.GenericEntitySynchronizationException;

/**
 * Implementations of {@link IdentityManager} are used to customize
 * synchronization runs executed by {@link GenericEntitySynchronization}.
 */
public interface IdentityManager {

	/**
	 * Called to determine whether this implementation is responsible for
	 * current synchronization instance
	 */
	boolean isResponsible(GenericEntity instanceToBeCloned, EntityType<? extends GenericEntity> entityType,
			SynchronizationContext context);

	/**
	 * Tries to find an existing entity based on the instanceToBeCloned. If no
	 * existing entity can be found in target session this method either returns
	 * null or - if an existing entity is required - throws a
	 * {@link EntityNotFoundInSessionException}
	 */
	GenericEntity findEntity(GenericEntity instanceToBeCloned, EntityType<? extends GenericEntity> entityType,
			SynchronizationContext context) throws GenericEntitySynchronizationException;

	/**
	 * Determines whether the given property of the synchronization instance
	 * should be transfered to the target session.
	 */
	boolean canTransferProperty(GenericEntity instanceToBeCloned, GenericEntity clonedInstance,
			EntityType<? extends GenericEntity> entityType, Property property, SynchronizationContext context);

}
