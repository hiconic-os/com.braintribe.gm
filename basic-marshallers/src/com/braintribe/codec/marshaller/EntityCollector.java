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
package com.braintribe.codec.marshaller;

import java.util.Collection;
import java.util.Map;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.BaseType;
import com.braintribe.model.generic.reflection.CollectionType;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EnumType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;

public abstract class EntityCollector {
	private final EntityScan deferringAnchor = new EntityScan(null, null);
	private EntityScan lastNode = deferringAnchor;
	private boolean directPropertyAccess;
	private int entityDepth;
	
	public void setDirectPropertyAccess(boolean directPropertyAccess) {
		this.directPropertyAccess = directPropertyAccess;
	}
	
	public void collect(Object value) {
		collect(BaseType.INSTANCE, value);
		EntityScan deferringNode = deferringAnchor.next;
		
		while (deferringNode != null) {
			collect(deferringNode.entity, deferringNode.entityType);
			deferringNode = deferringNode.next;
		}
	}
	
	private void collect(GenericModelType type, Object value) {
		if (value == null)
			return;
		
		while(true) {
			switch (type.getTypeCode()) {
			case objectType:
				type = type.getActualType(value);
				break;
				
			case entityType:
				GenericEntity entity = (GenericEntity)value;
				EntityType<GenericEntity> entityType = entity.entityType();
				if (add(entity, entityType)) {
					collect(entity, entityType);
				}
				return;
			case enumType:
				add((Enum<?>)value, (EnumType)type);
				return;
				
			case listType: 
			case setType:
				GenericModelType elementType = ((CollectionType)type).getCollectionElementType();
				for (Object element: (Collection<?>)value) {
					collect(elementType, element);
				}
				return;
			case mapType:
				GenericModelType[] parameterization = ((CollectionType)type).getParameterization();
				GenericModelType keyType = parameterization[0];
				GenericModelType valueType = parameterization[1];
				boolean handleKey = keyType.areCustomInstancesReachable();
				boolean handleValue = valueType.areCustomInstancesReachable();
				
				if (handleKey && handleValue) {
					for (Map.Entry<?, ?> entry: ((Map<?, ?>)value).entrySet()) {
						collect(keyType, entry.getKey());
						collect(valueType, entry.getValue());
					}
				}
				else if (handleKey) {
					for (Object mapKey: ((Map<?, ?>)value).keySet()) {
						collect(keyType, mapKey);
					}
				}
				else {
					for (Object mapValue: ((Map<?, ?>)value).values()) {
						collect(valueType, mapValue);
					}
				}
				return;
				
			default:
				return;
			}
		}
	}

	private void collect(GenericEntity entity, EntityType<?> entityType) {
		if (entityDepth == 50) {
			lastNode = lastNode.next = new EntityScan(entity, entityType);
			return;
		}
		
		entityDepth++;
		if (directPropertyAccess) {
			for (Property property: entityType.getCustomTypeProperties()) {
				Object value = property.getDirectUnsafe(entity);
				
				if (value != null) {
					collect(property.getType(), value);
				}
			}
		}
		else {
			for (Property property: entityType.getCustomTypeProperties()) {
				Object value = property.get(entity);
				
				if (value != null) {
					collect(property.getType(), value);
				}
			}
		}
		entityDepth--;
	}
	
	protected abstract boolean add(GenericEntity entity, EntityType<?> type);
	
	protected abstract void add(Enum<?> constant, EnumType type);

	
	private static class EntityScan {
		public EntityScan next;
		public GenericEntity entity;
		public EntityType<?> entityType;
		public EntityScan(GenericEntity entity, EntityType<?> entityType) {
			super();
			this.entity = entity;
			this.entityType = entityType;
		}
	}
	
}
