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
package com.braintribe.model.processing.manipulation.basic.mindelta;

import static com.braintribe.utils.lcd.CollectionTools2.newMap;
import static com.braintribe.utils.lcd.CollectionTools2.newSet;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.utils.lcd.CommonTools;

/**
 * @author peter.gazdik
 */
public class ChangeMapWithMinDelta {

	private static final Object DEFAULT = new Object();

	public static void apply(GenericEntity entity, Property property, Map<?, ?> currentMap, Map<?, ?> newMap) {
		/* if new size is less than half of old size, then it's definitely better to say new values rather than say what should be removed */
		if (2 * newMap.size() <= currentMap.size()) {
			if (currentMap.isEmpty()) {
				// both sets are empty
				return;
			}

			property.set(entity, newMap);
			return;
		}

		Set<Object> same = newSet();
		Set<Object> removed = newSet();
		Map<Object, Object> changed = newMap();

		Map<Object, Object> currentCasted = (Map<Object, Object>) currentMap;
		Map<Object, Object> newCasted = (Map<Object, Object>) newMap;

		for (Entry<Object, Object> entry: currentCasted.entrySet()) {
			Object key = entry.getKey();
			Object currentValue = entry.getValue();
			Object newValue = newCasted.getOrDefault(key, DEFAULT);

			if (newValue == DEFAULT) {
				removed.add(key);
				continue;
			}

			if (CommonTools.equalsOrBothNull(currentValue, newValue)) {
				same.add(key);

			} else {
				changed.put(key, newValue);
			}
		}

		int changeValueSize = newMap.size();
		int addAndRemoveSize = currentMap.size() + newMap.size() - 2 * same.size() - changed.size();

		if (addAndRemoveSize == 0) {
			// in other words the collections are equal
			return;
		}

		if (changeValueSize <= addAndRemoveSize) {
			property.set(entity, newMap);
			return;
		}

		for (Entry<Object, Object> entry: newCasted.entrySet()) {
			Object key = entry.getKey();
			if (!same.contains(key)) {
				changed.putIfAbsent(key, entry.getValue());
			}
		}

		currentCasted.keySet().removeAll(removed);
		currentCasted.putAll(changed);
	}

}
