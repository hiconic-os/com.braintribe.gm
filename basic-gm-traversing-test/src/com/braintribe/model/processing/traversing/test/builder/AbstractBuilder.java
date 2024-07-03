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
package com.braintribe.model.processing.traversing.test.builder;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;

/**
 * 
 */
public class AbstractBuilder<T extends GenericEntity, B extends AbstractBuilder<T, B>> {

	protected final B self;
	protected final Class<T> clazz;
	protected final T instance;

	@SuppressWarnings("unchecked")
	protected AbstractBuilder(Class<T> clazz) {
		EntityType<T> entityType = GMF.getTypeReflection().getEntityType(clazz);

		this.clazz = clazz;
		this.instance = entityType.createPlain();
		this.self = (B) this;
	}

	public T create() {
		return instance;
	}

}
