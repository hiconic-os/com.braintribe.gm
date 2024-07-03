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
package com.braintribe.model.processing.query.fluent;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;

/**
 * @deprecated Use {@link EntityQueryBuilder} for building {@link com.braintribe.model.query.EntityQuery}s or
 *             {@link PropertyQueryBuilder} for building {@link com.braintribe.model.query.PropertyQuery}
 */
@Deprecated
public class QueryBuilder extends EntityQueryBuilder {
	public static EntityQueryBuilder from(Class<? extends GenericEntity> clazz) {
		return QueryBuilder.from(clazz.getName());
	}

	public static EntityQueryBuilder from(EntityType<?> type) {
		return QueryBuilder.from(type.getTypeSignature());
	}

	public static EntityQueryBuilder from(String typeSignature) {
		return EntityQueryBuilder.from(typeSignature);
	}
}
