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
package com.braintribe.model.generic.session;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelTypeReflection;

interface GmSessionDeprecation {

	/**
	 * @deprecated use {@link GmSession#create(EntityType)}
	 */
	@Deprecated
	<T extends GenericEntity> T createEntity(EntityType<T> entityType);

	/**
	 * @deprecated use {@link GmSession#create(EntityType)}, by either using the type Literal or resolving the {@link EntityType} via
	 *             {@link GenericModelTypeReflection}.
	 */
	@Deprecated
	<T extends GenericEntity> T createEntity(Class<T> entityClass);

	/**
	 * @deprecated use {@link GmSession#create(EntityType)}, after resolving the {@link EntityType} via
	 *             {@link GenericModelTypeReflection#getType(String)}.
	 */
	@Deprecated
	<T extends GenericEntity> T createEntity(String typeSignature);

}
