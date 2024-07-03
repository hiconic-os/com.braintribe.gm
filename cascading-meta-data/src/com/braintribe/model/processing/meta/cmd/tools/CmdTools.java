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
package com.braintribe.model.processing.meta.cmd.tools;

import static com.braintribe.utils.lcd.CollectionTools2.newMap;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;

public class CmdTools {

	public static String asString(List<? extends Class<?>> classes) {
		StringBuilder sb = new StringBuilder();

		boolean first = true;
		for (Class<?> clazz: classes) {
			if (first) {
				first = false;
			} else {
				sb.append(", ");
			}

			sb.append(clazz.getName());
		}

		return sb.toString();
	}

	public static <T extends GenericEntity> Map<EntityType<? extends T>, T> indexByEntityType(Set<? extends T> set) {
		Map<EntityType<? extends T>, T> result = newMap();

		for (T t: set) {
			result.put(t.entityType(), t);
		}

		return result;
	}
}
