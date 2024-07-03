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

import java.util.Map;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.manipulation.LocalEntityProperty;
import com.braintribe.model.generic.manipulation.RemoveManipulation;
import com.braintribe.model.generic.reflection.CollectionType;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.processing.manipulator.api.ManipulatorContext;

public class RemoveManipulator extends AbstractCollectionManipulator<RemoveManipulation> {
	public static final RemoveManipulator defaultInstance = new RemoveManipulator();

	@Override
	public void apply(RemoveManipulation manipulation, ManipulatorContext context) {
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

		Map<Object, Object> itemsToRemove = manipulation.getItemsToRemove();

		GenericModelType keyType = getKeyType(collection, collectionType);
		GenericModelType valueType = getValueType(collectionType);
		GenericModelType mapType = GMF.getTypeReflection().getCollectionType(Map.class, keyType, valueType);

		Map<Object, Object> items = context.resolveValue(mapType, itemsToRemove);
		getCollectionManipulator(collection, collectionType).remove(collection, items);
	}

}
