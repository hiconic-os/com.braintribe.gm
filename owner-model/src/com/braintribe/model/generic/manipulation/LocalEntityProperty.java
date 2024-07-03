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

import com.braintribe.model.generic.GenericEntity;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.Property;

/* NOTE that other entities from this model have forward declarations in the RootModel. */


public interface LocalEntityProperty extends Owner {

	final EntityType<LocalEntityProperty> T = EntityTypes.T(LocalEntityProperty.class);

	GenericEntity getEntity();
	void setEntity(GenericEntity entity);

	// @formatter:off
//	 @Transient
//	 void setProperty(Property property);
//	  
//	  @Transient
//	  Property getProperty();
	// @formatter:on

	@Override
	default OwnerType ownerType() {
		return OwnerType.LOCAL_ENTITY_PROPERTY;
	}

	@Override
	default GenericEntity ownerEntity() {
		return getEntity();
	}
	
	@Override
	default EntityType<?> ownerEntityType() {
		GenericEntity entity = getEntity();
		if (entity == null)
			return null;

		return entity.entityType();
	}

	@Override
	// TODO OPTIMIZE
	default Property property() {
		Property property = null; // localEntityProperty.getProperty();

		// if (property != null)
		// return property;

		GenericEntity entity = getEntity();
		if (entity == null)
			return null;

		property = entity.entityType().findProperty(getPropertyName());

		// localEntityProperty.setProperty(property);

		return property;
	}

}
