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
package com.braintribe.model.generic.manipulation;

import static java.util.stream.Stream.concat;

import java.util.Map;
import java.util.stream.Stream;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface AddManipulation extends CollectionManipulation {

	EntityType<AddManipulation> T = EntityTypes.T(AddManipulation.class);

	/**
	 * Describes elements to add to a collection. The expected value depends on the actual collection type:
	 * <ul>
	 * <li>Map - keys are keys to be added, values are corresponding values</li>
	 * <li>Set - keys and values are the same - elements to add to the Set</li>
	 * 
	 * <li>List - keys are integers describing the new position of an element in the list and values are the elements to
	 * be added. So for example map with values. So assuming all indices are valid, after the manipulation is applied,
	 * the keys from this map will all be valid positions on which the corresponding values can be found. There are some
	 * extra rules when it comes to lists:
	 * <ul>
	 * <li>In case the key is a positive integer bigger than the size of the list, the element is appended to the
	 * end.</li>
	 * <li>In case the key is a negative integer, the position is taken from the end of the list. So -1 indicates the
	 * last position in the list (meaning the element added on position -1 will become the very last element of the
	 * list), position -2 indicates it will be 2nd to last element in the list.</li>
	 * <li>In case there are both positive and negative keys, this is equivalent to having two separate
	 * {@link AddManipulation}s, first one with only the positive keys, and second one with only the negative ones.</li>
	 * </ul>
	 * 
	 * </li>
	 * </ul>
	 * 
	 * In all cases, it might happen that we are trying to remove elements from a collection, that is not there, or (in
	 * 
	 * case of a map), we might remove something else than we thought we would.
	 * 
	 * @see RemoveManipulation#getItemsToRemove()
	 */
	Map<Object, Object> getItemsToAdd();
	void setItemsToAdd(Map<Object, Object> itemsToAdd);

	@Override
	@SuppressWarnings("unusable-by-js")
	default Stream<GenericEntity> touchedEntities() {
		Map<?, ?> items = getItemsToAdd();
		return concat(CollectionManipulation.super.touchedEntities(), concat( //
				PropertyManipulation.filterTouchedEntities(items.keySet().stream(), isRemote()),
				PropertyManipulation.filterTouchedEntities(items.values().stream(), isRemote())) //
		);
	}

	@Override
	default ManipulationType manipulationType() {
		return ManipulationType.ADD;
	}

}
