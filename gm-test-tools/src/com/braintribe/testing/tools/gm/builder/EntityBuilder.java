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
package com.braintribe.testing.tools.gm.builder;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.Property;

/**
 * @author peter.gazdik
 */
public class EntityBuilder {

	private final Consumer<GenericEntity> postConstructHandler;
	private final EntityType<?> entityType;
	private final GenericEntity entity;

	public EntityBuilder(EntityType<?> entityType, Function<EntityType<?>, GenericEntity> entityFactory,
			Consumer<GenericEntity> postConstructHandler) {

		this.entityType = entityType;
		this.postConstructHandler = postConstructHandler;
		this.entity = entityFactory.apply(entityType);
	}

	public EntityBuilder set(String propertyName, Object value) {
		entity.write(property(propertyName), value);
		return this;
	}

	public EntityBuilder add(String propertyName, Object value) {
		Collection<Object> collection = (Collection<Object>) entity.read(property(propertyName));
		collection.add(value);
		return this;
	}

	public EntityBuilder put(String propertyName, Object key, Object value) {
		Map<Object, Object> map = (Map<Object, Object>) entity.read(property(propertyName));
		map.put(key, value);
		return this;
	}

	public <T extends GenericEntity> T done() {
		postConstructHandler.accept(entity);
		return (T) entity;
	}

	private Property property(String propertyName) {
		return entityType.getProperty(propertyName);
	}

}
