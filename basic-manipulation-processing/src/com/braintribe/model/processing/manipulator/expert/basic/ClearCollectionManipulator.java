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
package com.braintribe.model.processing.manipulator.expert.basic;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.manipulation.ClearCollectionManipulation;
import com.braintribe.model.generic.manipulation.LocalEntityProperty;
import com.braintribe.model.generic.reflection.CollectionType;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.processing.manipulator.api.ManipulatorContext;

public class ClearCollectionManipulator extends AbstractCollectionManipulator<ClearCollectionManipulation> {
	public static final ClearCollectionManipulator defaultInstance = new ClearCollectionManipulator();

	@Override
	public void apply(ClearCollectionManipulation manipulation, ManipulatorContext context) {
		LocalEntityProperty owner = context.resolveOwner(manipulation);

		String propertyName = owner.getPropertyName();
		GenericEntity entity = owner.getEntity();
		EntityType<GenericEntity> entityType = entity.entityType();
		Property property = entityType.getProperty(propertyName);
		GenericModelType propertyType = property.getType().cast();
		CollectionType collectionType = propertyType instanceof CollectionType ? (CollectionType) propertyType : null;
		Object collection = entityType.getProperty(propertyName).get(entity);

		if (collection == null)
			return;

		getCollectionManipulator(collection, collectionType).clear(collection);
	}
}
