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
package com.braintribe.model.access.crud.support.resolver;

import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.Required;
import com.braintribe.model.access.crud.CrudExpertResolver;
import com.braintribe.model.access.crud.api.CrudExpert;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.processing.core.expert.api.GmExpertRegistry;

/**
 * Standard implementation of a {@link CrudExpertResolver} that takes a
 * {@link GmExpertRegistry} and uses it to resolve the requested
 * {@link CrudExpert} in {@link #getExpert(Class, EntityType)}.
 * 
 * @author gunther.schenk
 *
 */
public class RegistryBasedExpertResolver implements CrudExpertResolver {

	private GmExpertRegistry registry;

	// ***************************************************************************************************
	// Setter
	// ***************************************************************************************************

	@Configurable
	@Required
	public void setRegistry(GmExpertRegistry registry) {
		this.registry = registry;
	}

	// ***************************************************************************************************
	// CrudExpertResolver
	// ***************************************************************************************************

	@Override
	public <E extends CrudExpert<T>, T extends GenericEntity> E getExpert(Class<E> expertType,
			EntityType<T> requestedType) {
		return registry.getExpert(expertType).forType(requestedType);
	}

}
