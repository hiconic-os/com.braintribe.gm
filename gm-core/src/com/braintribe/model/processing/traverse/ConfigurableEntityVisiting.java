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
package com.braintribe.model.processing.traverse;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EnumType;

public class ConfigurableEntityVisiting extends EntityVisiting {

	private BiFunction<EntityType<?>, GenericEntity, Boolean> entityAdder;
	private BiConsumer<EnumType, Enum<?>> enumAdder;

	public ConfigurableEntityVisiting(BiFunction<EntityType<?>, GenericEntity, Boolean> entityAdder, BiConsumer<EnumType, Enum<?>> enumAdder) {
		super();
		this.entityAdder = entityAdder;
		this.enumAdder = enumAdder;
		this.visitEnums = true;
	}
	
	public ConfigurableEntityVisiting(BiFunction<EntityType<?>, GenericEntity, Boolean> entityAdder) {
		super();
		this.entityAdder = entityAdder;
		this.visitEnums = false;
	}

	@Override
	protected boolean add(GenericEntity entity, EntityType<?> type) {
		return entityAdder.apply(type, entity);
	}

	@Override
	protected void add(Enum<?> constant, EnumType type) {
		enumAdder.accept(type, constant);
	}

}
