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
package com.braintribe.model.processing.smood.population.index;

import static com.braintribe.utils.lcd.CollectionTools2.newSet;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.VdHolder;
import com.braintribe.model.processing.smood.population.info.IndexInfoImpl;

/**
 * 
 */
abstract class UniqueIndex extends SmoodIndex {

	protected final IndexInfoImpl indexInfo;
	protected final Map<Object, GenericEntity> map;
	protected final Set<GenericEntity> nullValueEntities;

	protected UniqueIndex(Map<Object, GenericEntity> map) {
		this.indexInfo = new IndexInfoImpl();
		this.map = map;
		this.nullValueEntities = newSet();
	}

	@Override
	public void addEntity(GenericEntity entity, Object value) {
		if (value == null) {
			nullValueEntities.add(entity);
			return;
		}

		for (SmoodIndex superRootIndex : superRootIndices) {
			GenericEntity otherEntity = superRootIndex.getValue(value);
			if (otherEntity != null && otherEntity != entity)
				throw new IllegalStateException("Another entity is already indexed (" + superRootIndex.getIndexInfo().getIndexId() + ") for key '"
						+ value + "'. ADDED ENTITY: " + entity + ", INDEXED ENTITY: " + otherEntity);
		}

		map.put(value, entity);
	}

	@Override
	public void onChangeValue(GenericEntity entity, Object oldValue, Object newValue) {
		removeEntity(entity, oldValue);

		try {
			addEntity(entity, newValue);

		} catch (IllegalStateException e) {
			addEntity(entity, oldValue);
			throw e;
		}
	}

	@Override
	public void removeEntity(GenericEntity entity, Object value) {
		GenericEntity removedEntity = actualRemove(value, entity);
		if (removedEntity != entity) {
			// this can only happen if value != null, so we do not have to handle that case
			map.put(value, removedEntity);
			throw new IllegalStateException("Different entity found in index (" + indexInfo.getIndexId() + ") for key '" + value + "', EXPECTED: "
					+ entity + ", FOUND : " + removedEntity);
		}
	}

	private GenericEntity actualRemove(Object key, GenericEntity entity) {
		if (key == null) {
			nullValueEntities.remove(entity);
			return entity;

		} else if (VdHolder.isVdHolder(key)) {
			return entity;

		} else {
			return map.remove(key);
		}
	}

	@Override
	protected Collection<? extends GenericEntity> getThisLevelValues(Object indexValue) {
		if (indexValue == null)
			return nullValueEntities;

		GenericEntity value = getValue(indexValue);

		return value == null ? Collections.<GenericEntity> emptySet() : Arrays.asList(value);
	}

	@Override
	protected GenericEntity getThisLevelValue(Object indexValue) {
		return indexValue == null ? anyNullValue() : map.get(indexValue);
	}

	private GenericEntity anyNullValue() {
		return nullValueEntities.isEmpty() ? null : nullValueEntities.iterator().next();
	}

	@Override
	protected Collection<? extends GenericEntity> allThisLevelValues() {
		return nullValueEntities.isEmpty() ? map.values() : union(nullValueEntities, map.values());
	}

	private static Set<GenericEntity> union(Collection<GenericEntity> c1, Collection<GenericEntity> c2) {
		// TODO there is no reason here to copy the data, just create a view which combines both
		Set<GenericEntity> result = newSet();
		result.addAll(c1);
		result.addAll(c2);

		return result;
	}

	@Override
	public IndexInfoImpl getIndexInfo() {
		return indexInfo;
	}

}
