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
package com.braintribe.model.generic.manipulation;

import java.util.stream.Stream;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * 
 */
public interface VoidManipulation extends AtomicManipulation {

	EntityType<VoidManipulation> T = EntityTypes.T(VoidManipulation.class);

	@Override
	default GenericEntity manipulatedEntity() {
		return null;
	}

	@Override
	default boolean isRemote() {
		return false;
	}

	@Override
	@SuppressWarnings("unusable-by-js")
	default Stream<AtomicManipulation> stream() {
		return Stream.empty();
	}

	@Override
	@SuppressWarnings("unusable-by-js")
	default Stream<GenericEntity> touchedEntities() {
		return Stream.empty();
	}

	@Override
	default ManipulationType manipulationType() {
		return ManipulationType.VOID;
	}

}
