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
package com.braintribe.model.processing.query.test.builder;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.processing.smood.Smood;

/**
 * 
 */
public class AbstractBuilder<T extends GenericEntity, B extends AbstractBuilder<T, B>> {

	protected final B self;
	protected final Smood smood;
	protected final Class<T> clazz;
	protected final EntityType<T> entityType;
	protected final T instance;

	protected AbstractBuilder(Class<T> clazz, Smood smood) {
		this.clazz = clazz;
		this.smood = smood;

		this.entityType = GMF.getTypeReflection().getEntityType(clazz);
		this.instance = newInstance();
		this.self = (B) this;
	}

	protected T newInstance() {
		return entityType.create();
	}

	public T create() {
		return create(smood != null);
	}

	public T create(boolean register) {
		return register ? createAndRegister(true) : instance;
	}

	private T createAndRegister(boolean generateId) {
		smood.registerEntity(instance, generateId);

		return instance;
	}

	public B id(Object value) {
		instance.setId(value);
		return self;
	}

	public B partition(String value) {
		instance.setPartition(value);
		return self;
	}

	public B globalId(String globalId) {
		instance.setGlobalId(globalId);
		return self;
	}
	
}
