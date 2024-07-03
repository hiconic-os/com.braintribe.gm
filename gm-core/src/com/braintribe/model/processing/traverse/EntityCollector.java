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

import java.util.HashSet;
import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EnumType;

public class EntityCollector extends EntityVisiting {
	private final Set<GenericEntity> entities = new HashSet<>();
	private final Set<Enum<?>> enums = new HashSet<>();
	private final Set<EntityType<?>> entityTypes = new HashSet<>();
	private final Set<EnumType> enumTypes = new HashSet<>();
	private boolean collectEnums;
	private boolean collectEnumTypes;
	private boolean collectEntityTypes;
	
	public Set<GenericEntity> getEntities() {
		return entities;
	}
	
	public Set<EntityType<?>> getEntityTypes() {
		return entityTypes;
	}
	
	public Set<Enum<?>> getEnums() {
		return enums;
	}
	
	public Set<EnumType> getEnumTypes() {
		return enumTypes;
	}
	
	public void setCollectEntityTypes(boolean collectEntityTypes) {
		this.collectEntityTypes = collectEntityTypes;
	}
	
	
	public void setCollectEnums(boolean collectEnums) {
		this.collectEnums = collectEnums;
	}
	
	public void setCollectEnumTypes(boolean collectEnumTypes) {
		this.collectEnumTypes = collectEnumTypes;
	}
	
	@Override
	protected boolean add(GenericEntity entity, EntityType<?> type) {
		boolean result = entities.add(entity);
		if (result && collectEntityTypes) {
			entityTypes.add(type);
		}
		
		return result;
	}
	
	@Override
	protected void add(Enum<?> constant, EnumType type) {
		if (collectEnums) {
			if (enums.add(constant) && collectEnumTypes) {
				enumTypes.add(type);
			}
		}
		else
			enumTypes.add(type);
	}
	
	protected boolean add(EnumType enumType) {
		return enumTypes.add(enumType);
	}

	protected boolean add(EntityType<?> entityType) {
		return entityTypes.add(entityType);
	}
}
