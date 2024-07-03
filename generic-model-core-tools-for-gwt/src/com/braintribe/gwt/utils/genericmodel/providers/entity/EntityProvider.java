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
package com.braintribe.gwt.utils.genericmodel.providers.entity;

import java.util.function.Function;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.value.EntityReference;

/**
 * An <code>EntityProvider</code> provides {@link GenericEntity entities} referenced by {@link EntityReference}s.
 *
 * @author michael.lafite
 */
public interface EntityProvider extends Function<EntityReference, GenericEntity> {

	/**
	 * Looks up and returns the entity referenced by the passed <code>entityReference</code>.
	 *
	 * @param entityReference
	 *            the entity reference for the searched entity.
	 * @return the searched entity
	 * @throws EntityNotFoundException
	 *             if the entity doesn't exist.
	 * @throws EntityLookupException
	 *             if an error occurs while looking up the entity.
	 * @throws UnsupportedEntityReferenceTypeException
	 *             if the passed <code>entityReference</code> type is not supported.
	 */
	@Override
	public GenericEntity apply(EntityReference entityReference)
			throws EntityLookupException, EntityNotFoundException, UnsupportedEntityReferenceTypeException;
}
