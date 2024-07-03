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
package com.braintribe.model.processing.manipulation.parser.impl.collection;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.braintribe.model.generic.reflection.CollectionType.CollectionKind;
import com.braintribe.model.generic.reflection.LinearCollectionType;
import com.braintribe.model.generic.reflection.MapType;
import com.braintribe.model.processing.manipulation.parser.api.GmmlConstants;
import com.braintribe.model.processing.manipulation.parser.api.GmmlManipulatorErrorHandler;

/**
 * @author peter.gazdik
 */
public class GmmlCollectionTools implements GmmlConstants {

	public static Collection<Object> convertToCollection(Object value) {
		if (value instanceof Collection)
			return (Collection<Object>) value;
		else if (value instanceof Map)
			// TODO really values? cause opposite direction uses keys
			return ((Map<Object, Object>) value).values();
		else
			return Collections.singleton(value);
	}

	public static void addToCollection(Collection<Object> collection, LinearCollectionType type, Object value, GmmlManipulatorErrorHandler errorHandler) {
		if (value != missing) {
			if (type.getCollectionElementType().isValueAssignable(value)) {
				collection.add(value);
				return;

			} else {
				errorHandler.wrongValueTypeToAddToCollection(value, type);
			}
		}

		// value is missing OR incompatible type and we are lenient
		if (type.getCollectionKind() == CollectionKind.list)
			collection.add(null);
	}

	public static Map<Object, Object> convertToMap(Object value) {
		if (value instanceof Map)
			return (Map<Object, Object>) value;
		else if (value instanceof Collection)
			return ((Collection<Object>) value).stream().collect(Collectors.toMap(Function.identity(), x -> null));
		else
			return Collections.singletonMap(value, value);
	}

	public static void putToMap(Map<Object, Object> map, MapType type, Object key, Object value, GmmlManipulatorErrorHandler errorHandler) {
		if (key == missing || value == missing)
			return;

		boolean keyOk = type.getKeyType().isValueAssignable(key);
		boolean valueOk = type.getValueType().isValueAssignable(value);

		if (!keyOk || !valueOk)
			errorHandler.wrongTypeForMapPut(key, keyOk, value, valueOk, type);

		if (!keyOk)
			return;

		if (!valueOk) {
			map.put(key, null);
			return;
		}

		map.put(key, value);
	}

}
