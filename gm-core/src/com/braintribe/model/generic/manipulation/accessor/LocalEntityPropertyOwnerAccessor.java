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
package com.braintribe.model.generic.manipulation.accessor;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.enhance.EnhancedEntity;
import com.braintribe.model.generic.manipulation.LocalEntityProperty;
import com.braintribe.model.generic.pr.AbsenceInformation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.value.EntityReference;

public class LocalEntityPropertyOwnerAccessor extends OwnerAccessor<LocalEntityProperty> {

	@SuppressWarnings("unchecked")
	@Override
	public <T1> T1 get(LocalEntityProperty owner) {
		String propertyName = owner.getPropertyName();
		GenericEntity entity = owner.getEntity();
		EntityType<GenericEntity> entityType = entity.entityType();
		return (T1) entityType.getProperty(propertyName).get(entity);
	}

	@Override
	public EntityReference replace(LocalEntityProperty owner, Object newValue) {
		String propertyName = owner.getPropertyName();
		GenericEntity entity = owner.getEntity();
		
		EntityType<GenericEntity> entityType = entity.entityType();
		Property property = entityType.getProperty(propertyName);
		property.set(entity, newValue);
		
		if (property.isIdentifier()) {
			return entity.reference();
		} else
			return null;
	}

	@Override
	public void markAsAbsent(LocalEntityProperty owner, AbsenceInformation absenceInformation, String propertyName) {
		GenericEntity entity = owner.getEntity();
		if (entity instanceof EnhancedEntity) {
			entity.entityType().getProperty(propertyName).setAbsenceInformation(entity, GMF.absenceInformation());
		}
	}

}
