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
package com.braintribe.model.processing.clone;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.ListType;
import com.braintribe.model.generic.reflection.MapType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.SetType;

@SuppressWarnings("unused")
public interface CloningVisitor {

	default void enterRootValue(GenericModelType type, Object value) {
		// empty
	}
	
	default void leaveRootValue(GenericModelType type, Object value) {
		// empty
	}
	
	default void enterEntity(GenericEntity entity, GenericEntity clonedEntity) {
		// empty
	}
	
	default void leaveEntity(GenericEntity entity, GenericEntity clonedEntity) {
		// empty
	}
	
	default void enterPropertyValue(GenericEntity entity, GenericEntity clonedEntity, Property property, GenericModelType type, Object value) {
		// empty
	}
	
	default void leavePropertyValue(GenericEntity entity, GenericEntity clonedEntity,  Property property, GenericModelType type, Object value) {
		// empty
	}
	
	default void enterListElement(ListType listType, List<?> list, int index, GenericModelType type, Object value) {
		// empty
	}
	
	default void leaveListElement(ListType listType, List<?> list, int index, GenericModelType type, Object value) {
		// empty
	}
	
	default void enterSetElement(SetType setType, Set<?> set, GenericModelType type, Object value) {
		// empty
	}
	
	default void leaveSetElement(SetType setType, Set<?> set, GenericModelType type, Object value) {
		// empty
	}
	
	default void enterMapKey(MapType mapType, Map<?, ?> map, GenericModelType type, Object value) {
		// empty
	}
	
	default void leaveMapKey(MapType mapType, Map<?, ?> map, GenericModelType type, Object value) {
		// empty
	}
	
	default void enterMapValue(MapType mapType, Map<?, ?> map, GenericModelType keyType, Object key, GenericModelType type, Object value) {
		// empty
	}
	
	default void leaveMapValue(MapType mapType, Map<?, ?> map, GenericModelType keyType, Object key, GenericModelType type, Object value) {
		// empty
	}
}
