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
package com.braintribe.model.processing.query.stringifier.experts;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.reflection.CollectionType;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.GenericModelTypeReflection;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.query.From;
import com.braintribe.model.query.Join;
import com.braintribe.model.query.Source;

/**
 * 
 */
/* package */ class PropertyResolver {

	private static final GenericModelTypeReflection typeReflection = GMF.getTypeReflection();

	public static Property resolveProperty(Source source, String propertyPath, String defaultType) {
		EntityType<?> entityType = resolveType(source, defaultType);
		if (entityType == null)
			return null;

		String[] propertyNames = propertyPath.split("\\.");
		int counter = 0;
		for (String propertyName : propertyNames) {
			boolean isLast = ++counter == propertyNames.length;
			Property property = entityType.findProperty(propertyName);

			if (property == null || isLast)
				return property;

			GenericModelType propertyType = property.getType();

			if (!propertyType.isEntity())
				return null;

			entityType = (EntityType<?>) propertyType;
		}

		return null;
	}

	private static <T extends GenericModelType> T resolveType(Source source, String defaultType) {
		if (source == null)
			return (T) typeReflection.findEntityType(defaultType);

		if (source instanceof From) {
			return (T) typeReflection.findEntityType(((From) source).getEntityTypeSignature());

		} else {
			Join join = (Join) source;
			return (T) resolvePropertyTypeHelper(join.getSource(), join.getProperty(), defaultType);
		}
	}

	private static GenericModelType resolvePropertyTypeHelper(Source source, String propertyName, String defaultType) {
		EntityType<?> entityType = resolveType(source, defaultType);
		if (entityType == null)
			return null;

		Property property = entityType.findProperty(propertyName);
		if (property == null)
			return null;

		GenericModelType propertyType = property.getType();

		switch (propertyType.getTypeCode()) {
			case listType:
			case mapType:
			case setType:
				return resolveCollectionElementType(propertyType);
			default:
				break;
		}

		return propertyType;
	}

	private static GenericModelType resolveCollectionElementType(GenericModelType propertyType) {
		return ((CollectionType) propertyType).getCollectionElementType();
	}

}
