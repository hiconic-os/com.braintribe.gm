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
package com.braintribe.model.processing.query.eval.tools;

import java.util.Collection;
import java.util.Map;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.CollectionType;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.TypeCode;
import com.braintribe.model.processing.query.eval.api.RuntimeQueryEvaluationException;

/**
 * 
 */
public class PropertyPathResolver {

	public static Object resolvePropertyPath(GenericEntity entity, String propertyPath) {
		if (entity == null) {
			return null;
		}
		// Note: in most cases, the propertyPath does not contain a '.'. Hence, it is quicker to search for a 
		// dot before applying an expensive split operation
		// Tests showed that this is way faster (and also faster than searching for a dot manually in the char[] of the string)
		if (propertyPath.indexOf('.') != -1) {
			return resolvePropertyPath(entity, propertyPath.split("\\."));
		} else {
			return resolvePropertyPath(entity, new String[] {propertyPath});
		}
	}

	private static Object resolvePropertyPath(Object object, String[] propertyPath) {
		for (String propertyName: propertyPath) {
			if (object == null) {
				return null;
			}

			GenericEntity entity = (GenericEntity) object;
			EntityType<GenericEntity> et = entity.entityType();
			object = et.getProperty(propertyName).get(entity);
			object = getNullIfEmptyCollection(object);
		}

		return object;
	}

	private static Object getNullIfEmptyCollection(Object object) {
		if (object instanceof Collection && ((Collection<?>) object).isEmpty()) {
			return null;
		}
		if (object instanceof Map && ((Map<?, ?>) object).isEmpty()) {
			return null;
		}

		return object;
	}

	public static GenericModelType resolvePropertyPathType(GenericModelType valueType, String propertyPath) {
		return resolvePropertyPathType(valueType, propertyPath.split("\\."));
	}

	private static GenericModelType resolvePropertyPathType(GenericModelType type, String[] propertyPath) {
		int len = propertyPath.length;
		for (int i = 0; i < len; i++) {
			String propertyName = propertyPath[i];

			EntityType<?> et = extractEntityType(type);

			if (et == null) {
				throwPropertyPathException(type, propertyPath, i);
			}

			type = et.getProperty(propertyName).getType();
		}

		return type;
	}

	private static EntityType<?> extractEntityType(GenericModelType type) {
		TypeCode typeCode = type.getTypeCode();

		switch (typeCode) {
			case entityType:
				return (EntityType<?>) type;

			case listType:
			case setType:
			case mapType:
				return extractEntityType(((CollectionType) type).getCollectionElementType());
				
			default:
				return null;
		}

	}

	private static void throwPropertyPathException(GenericModelType type, String[] propertyPath, int counter) {
		String path = type.getTypeSignature();
		for (int i = 0; i <= counter; i++) {
			path += "." + propertyPath[i];
		}

		throw new RuntimeQueryEvaluationException("Unable to resolve entity type for path: " + path);
	}

}
