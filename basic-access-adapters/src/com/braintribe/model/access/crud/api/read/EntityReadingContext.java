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
package com.braintribe.model.access.crud.api.read;

import com.braintribe.model.access.crud.api.DataReadingContext;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;

/**
 * A {@link DataReadingContext} implementation provided to {@link EntityReader} experts containing 
 * necessary informations on the expected instance.
 *  
 * @author gunther.schenk
 */
public interface EntityReadingContext<T extends GenericEntity> extends DataReadingContext<T> {
	
	/**
	 * @return the id value of the expected instance.
	 */
	<I> I getId();

	/**
	 * @return the actual requested type of the expected instance.
	 */
	EntityType<?> getRequestedType();

	/**
	 * Static helper method to build a new {@link EntityReadingContext} instance.
	 */
	static <T extends GenericEntity, I> EntityReadingContext<T> create(EntityType<T> requestedType, I id) {
		return create(requestedType, id, null);
	}
	
	/**
	 * Static helper method to build a new {@link EntityReadingContext} instance with query context.
	 */
	static <T extends GenericEntity, I> EntityReadingContext<T> create(EntityType<T> requestedType, I id, QueryContext context) {
		return new EntityReadingContext<T>() {
			@Override
			public I getId() {
				return id;
			}
			@Override
			public EntityType<?> getRequestedType() {
				return requestedType;
			}
			@Override
			public QueryContext getQueryContext() {
				return context;
			}
		};
	}
	
	
	
	
}
