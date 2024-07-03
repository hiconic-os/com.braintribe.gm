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
package com.braintribe.model.processing.query.building;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.manipulation.EntityProperty;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.value.PersistentEntityReference;
import com.braintribe.model.query.Operand;
import com.braintribe.model.query.PropertyOperand;
import com.braintribe.model.query.PropertyQuery;

public abstract class PropertyQueries extends Queries {
	
	
	public static PropertyQuery from(EntityType<?> entityType, Object id, String propertyName) {
		return PropertyQuery.create(entityType, id, propertyName);
	}
	
	public static PropertyQuery from(EntityType<?> entityType, Object id, Property property) {
		return PropertyQuery.create(entityType, id, property);
	}
	
	public static PropertyQuery from(EntityType<?> entityType, Object id, String partition, String propertyName) {
		return PropertyQuery.create(entityType, id, partition, propertyName);
	}
	
	public static PropertyQuery from(EntityType<?> entityType, Object id, String partition, Property property) {
		return PropertyQuery.create(entityType, id, partition, property);
	}
	
	public static PropertyQuery from(String entityTypeSignature, Object id, String propertyName) {
		return PropertyQuery.create(entityTypeSignature, id, propertyName);
	}
	
	public static PropertyQuery from(String entityTypeSignature, Object id, Property property) {
		return PropertyQuery.create(entityTypeSignature, id, property);
	}
	
	public static PropertyQuery from(String entityTypeSignature, Object id, String partition, String propertyName) {
		return PropertyQuery.create(entityTypeSignature, id, partition, propertyName);
	}
	
	public static PropertyQuery from(String entityTypeSignature, Object id, String partition, Property property) {
		return PropertyQuery.create(entityTypeSignature, id, partition, property);
	}
	
	public static PropertyQuery from(GenericEntity instance, String propertyName) {
		return PropertyQuery.create(instance, propertyName);
	}
	
	public static PropertyQuery from(GenericEntity instance, Property property) {
		return PropertyQuery.create(instance, property);
	}
	
	public static PropertyQuery from(PersistentEntityReference reference, String propertyName) {
		return PropertyQuery.create(reference, propertyName);
	}
	
	public static PropertyQuery from(PersistentEntityReference reference, Property property) {
		return PropertyQuery.create(reference, property);
	}
	
	public static PropertyQuery from(EntityProperty entityProperty) {
		return PropertyQuery.create(entityProperty);
	}
	
	public static PropertyOperand property(String name) {
		return PropertyOperand.create(name);
	}
	
	public static PropertyOperand property(Property property) {
		return PropertyOperand.create(property);
	}
	
	public static Operand source() {
		return null;
	}
}