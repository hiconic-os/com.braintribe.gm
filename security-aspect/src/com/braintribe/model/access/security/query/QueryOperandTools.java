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
package com.braintribe.model.access.security.query;

import com.braintribe.model.generic.reflection.CollectionType;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.query.PropertyOperand;

/**
 * 
 */
class QueryOperandTools {

	public static EntityTypeProperty resolveEntityProperty(PropertyOperand po, SourcesDescriptor querySources) {
		EntityType<?> sourceType = querySources.getSourceType(po.getSource());
		String propertyPath = po.getPropertyName();

		String lastPropertyName = extractLastPropertyName(propertyPath);
		String pathToOwner = extractAllExceptLastProperty(propertyPath);

		EntityType<?> lastPropertyOwnerType = resolveEntityType(sourceType, pathToOwner, false);

		return new EntityTypeProperty(lastPropertyOwnerType, lastPropertyName);
	}

	static class EntityTypeProperty {
		public EntityType<?> entityType;
		public String propertyName;

		public EntityTypeProperty(EntityType<?> entityType, String propertyName) {
			this.entityType = entityType;
			this.propertyName = propertyName;
		}

		@Override
		public String toString() {
			return entityType.getTypeSignature() + "." + propertyName;
		}
	}

	private static String extractLastPropertyName(String path) {
		return path.contains(".") ? path.substring(path.lastIndexOf(".") + 1) : path;
	}

	private static String extractAllExceptLastProperty(String path) {
		return path.contains(".") ? path.substring(0, path.lastIndexOf(".")) : null;
	}

	private static EntityType<?> resolveEntityType(EntityType<?> sourceType, String path, boolean resolveCollectionType) {
		return (EntityType<?>) resolveType(sourceType, path, resolveCollectionType);
	}

	private static GenericModelType resolveType(EntityType<?> entityType, String path, boolean resolveCollectionType) {
		if (path == null) {
			return entityType;
		}

		Property p = resolveProperty(entityType, path);
		GenericModelType type = p.getType();

		if ((type instanceof CollectionType) && resolveCollectionType) {
			type = resolveCollectionElementType(type);
		}

		return type;
	}

	private static Property resolveProperty(EntityType<?> entityType, String path) {
		EntityType<?> startingEntityType = entityType;

		Property property = null;
		GenericModelType type = null;

		String[] properties = path.split("\\.");
		int counter = 0;
		for (String propertyName: properties) {
			boolean isLast = ++counter == properties.length;

			property = entityType.getProperty(propertyName);
			type = property.getType();

			if (!isLast) {
				if (!(type instanceof EntityType)) {
					throw new RuntimeException("Illegal attempt to dereference a type (" + startingEntityType.getTypeSignature() + "." +
							buildPropertyChain(properties, counter) + ".[" + properties[counter] + "])");
				}

				entityType = (EntityType<?>) type;

			}
		}

		return property;
	}

	private static String buildPropertyChain(String[] properties, int counter) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < counter; i++) {
			if (i > 0) {
				sb.append(',');
			}
			sb.append(properties[i]);
		}

		return sb.toString();
	}

	private static GenericModelType resolveCollectionElementType(GenericModelType propertyType) {
		return ((CollectionType) propertyType).getCollectionElementType();
	}
}
