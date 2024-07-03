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
package com.braintribe.codec.marshaller.common;

import java.util.Optional;
import java.util.function.Function;

import com.braintribe.codec.marshaller.api.ScalarEntityParsers;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.processing.core.expert.impl.PolymorphicDenotationMap;

public class ConfigurableScalarEntityParsers implements ScalarEntityParsers {
	private PolymorphicDenotationMap<GenericEntity, Function<String, ? extends GenericEntity>> experts = new PolymorphicDenotationMap<GenericEntity, Function<String,? extends GenericEntity>>();

	public <T extends GenericEntity> void addParser(EntityType<T> type, Function<String, T> parser) {
		experts.put(type, parser);
	}
	
	@Override
	public <T extends GenericEntity> Optional<Function<String, T>> findEntityScalarParser(EntityType<T> type) {
		return Optional.ofNullable(experts.find(type));
	}
}
