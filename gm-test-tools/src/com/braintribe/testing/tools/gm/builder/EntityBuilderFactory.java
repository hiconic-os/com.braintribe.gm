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

import java.util.function.Consumer;
import java.util.function.Function;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;

/**
 * @author peter.gazdik
 */
public class EntityBuilderFactory {

	private static final Function<EntityType<?>, GenericEntity> defaultEntityFactory = EntityType::create;
	private static final Consumer<GenericEntity> defaultPostConstructHandler = e -> {
		/* no post construct handling */};

	private final Function<EntityType<?>, GenericEntity> entityFactory;
	private final Consumer<GenericEntity> postConstructHandler;

	public EntityBuilderFactory() {
		this(null, null);
	}

	public EntityBuilderFactory(Function<EntityType<?>, GenericEntity> entityFactory) {
		this(entityFactory, null);
	}

	public EntityBuilderFactory(Function<EntityType<?>, GenericEntity> entityFactory, Consumer<GenericEntity> postConstructHandler) {
		this.entityFactory = entityFactory != null ? entityFactory : defaultEntityFactory;
		this.postConstructHandler = postConstructHandler != null ? postConstructHandler : defaultPostConstructHandler;
	}

	public EntityBuilder make(EntityType<?> entityType) {
		return new EntityBuilder(entityType, entityFactory, postConstructHandler);
	}

}
