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
package com.braintribe.model.access.crud;

import com.braintribe.model.access.crud.api.CrudExpert;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;

/**
 * Implementations are required to return concrete {@link CrudExpert} instances (of the given expertType) registered 
 * for the requested {@link EntityType}.
 * 
 * @author gunther.schenk
 */
public interface CrudExpertResolver {

	/**
	 * @return an instance of the passed expertType registered for the requested {@link EntityType}.
	 */
	<E extends CrudExpert<T>, T extends GenericEntity> E getExpert(Class<E> expertType, EntityType<T> requestedType);
	
}
