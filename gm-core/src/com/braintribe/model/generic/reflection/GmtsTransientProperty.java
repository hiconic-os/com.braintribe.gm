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
package com.braintribe.model.generic.reflection;

import java.util.function.BiConsumer;
import java.util.function.Function;

import com.braintribe.model.generic.GenericEntity;

/**
 * @author peter.gazdik
 */
public class GmtsTransientProperty extends AbstractTransientProperty {

	private final EntityType<?> declaringType;

	public GmtsTransientProperty(EntityType<?> declaringType, String name, Class<?> type, //
			Function<? extends GenericEntity, ?> getter, BiConsumer<? extends GenericEntity, ?> setter) {

		super(name, type, (Function<GenericEntity, Object>) getter, (BiConsumer<GenericEntity, Object>) setter);
		this.declaringType = declaringType;
	}

	@Override
	public EntityType<?> getDeclaringType() {
		return declaringType;
	}

}
