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
import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.value.EntityReference;

@Abstract
public interface PropertyManipulation extends AtomicManipulation {

	EntityType<PropertyManipulation> T = EntityTypes.T(PropertyManipulation.class);

	/** Is always {@link EntityProperty} or {@link LocalEntityProperty} */
	void setOwner(Owner owner);
	Owner getOwner();

	@Override
	default boolean isRemote() {
		return getOwner().ownerType() == OwnerType.ENTITY_PROPERTY;
	}

	@Override
	default GenericEntity manipulatedEntity() {
		return getOwner().ownerEntity();
	}

	@Override
	@SuppressWarnings("unusable-by-js")
	default Stream<GenericEntity> touchedEntities() {
		return Stream.of(getOwner().ownerEntity());
	}

	static Stream<GenericEntity> filterTouchedEntities(Stream<?> values, boolean isRemote) {
		EntityType<?> filterType = isRemote ? EntityReference.T : GenericEntity.T;
		return (Stream<GenericEntity>) values.filter(value -> filterType.isInstance(value));
	}

}
