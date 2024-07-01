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
package com.braintribe.model.processing.smood.population.index;

import static com.braintribe.utils.lcd.CollectionTools2.asList;
import static com.braintribe.utils.lcd.CollectionTools2.newList;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;

/**
 * Base for a smood index (unique and multi) which directly contains only instances of this {@link #entityType type}, but no sub-type, to reduce
 * memory footprint. It contains all the logic related to storing instances for given type, but can resolve instances in a polymorphic way, because it
 * is aware of it's {@link #meAndSubIndices sub-types} and {@link #superRootIndices super-types}.
 * 
 * @see #getValue(Object)
 * @see #getValues(Object)
 * @see #allValues()
 * 
 * @author peter.gazdik
 */
public abstract class SmoodIndex implements LookupIndex {

	protected EntityType<?> entityType = GenericEntity.T;
	protected final List<SmoodIndex> superRootIndices = asList(this);
	protected final List<SmoodIndex> meAndSubIndices = asList(this);

	public void setEntityType(EntityType<?> entityType) {
		this.entityType = entityType;
	}

	public void w_linkSuperIndex(SmoodIndex superIndex) {
		superIndex.meAndSubIndices.add(this);

		for (SmoodIndex superRootIndex : superRootIndices)
			if (superRootIndex.entityType.isAssignableFrom(superIndex.entityType))
				// the new superIndex is a sub-type of some superRoot
				return;

		// the new superIndes is a new superRoot, now we remove all existing superRoots that are it's sub-types
		Iterator<SmoodIndex> it = superRootIndices.iterator();
		while (it.hasNext()) {
			SmoodIndex superRootIndex = it.next();
			// we skip indices for types assignable to superIndex.entityType, because they are covered by this new super index
			if (superIndex.entityType.isAssignableFrom(superRootIndex.entityType))
				it.remove();
		}

		superRootIndices.add(superIndex);
	}

	@Override
	public final <T extends GenericEntity> T getValue(Object indexValue) {
		for (SmoodIndex index : meAndSubIndices) {
			GenericEntity entity = index.getThisLevelValue(indexValue);
			if (entity != null)
				return (T) entity;
		}

		return null;
	}

	protected abstract GenericEntity getThisLevelValue(Object indexValue);

	@Override
	public final Collection<? extends GenericEntity> getValues(Object indexValue) {
		List<GenericEntity> result = newList();

		for (SmoodIndex index : meAndSubIndices)
			result.addAll(index.getThisLevelValues(indexValue));

		return result;
	}

	protected abstract Collection<? extends GenericEntity> getThisLevelValues(Object indexValue);

	@Override
	public final Collection<? extends GenericEntity> allValues() {
		List<GenericEntity> result = newList();

		for (SmoodIndex index : meAndSubIndices)
			result.addAll(index.allThisLevelValues());

		return result;
	}

	protected abstract Collection<? extends GenericEntity> allThisLevelValues();

}
