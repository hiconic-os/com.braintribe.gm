// ============================================================================
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
// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
// ============================================================================
package com.braintribe.model.processing.meta.cmd.context.tools;

import static com.braintribe.model.processing.meta.cmd.tools.CmdGwtUtils.newCacheMap;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.braintribe.model.meta.GmEntityType;

/**
 * 
 */
// 28.2.2014: Note that this is not used right now, but I do not want to delete it just yet.
public class ModelInfoResolver {

	private final Map<GmEntityType, Set<GmEntityType>> map = newCacheMap();

	public Set<GmEntityType> getAllSuperTypesFor(GmEntityType type) {
		Set<GmEntityType> set = map.get(type);

		if (set == null) {
			set = computeAllSuperTypesFor(type);
			map.put(type, set);
		}

		return set;
	}

	private Set<GmEntityType> computeAllSuperTypesFor(GmEntityType type) {
		Set<GmEntityType> result = new HashSet<GmEntityType>();

		addSuperTypes(result, type);

		return Collections.unmodifiableSet(result);
	}

	private void addSuperTypes(Set<GmEntityType> result, GmEntityType type) {
		result.add(type);

		List<GmEntityType> superTypes = type.getSuperTypes();

		if (superTypes == null) {
			return;
		}

		for (GmEntityType superType: superTypes) {
			addSuperTypes(result, superType);
		}
	}
}
