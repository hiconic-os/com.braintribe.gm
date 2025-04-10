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

import java.util.Collection;
import java.util.Comparator;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.VdHolder;
import com.braintribe.model.processing.query.eval.tools.EntityComparator;
import com.braintribe.model.processing.smood.population.SmoodIndexTools;
import com.braintribe.model.processing.smood.population.info.IndexInfoImpl;
import com.braintribe.utils.collection.api.NavigableMultiMap;
import com.braintribe.utils.collection.impl.ComparatorBasedNavigableMultiMap;
import com.braintribe.utils.collection.impl.NullHandlingComparator;

/**
 * 
 */
public abstract class MultiIndex extends SmoodIndex {

	protected final IndexInfoImpl indexInfo;
	protected final Comparator<Object> keyComparator;
	protected final NavigableMultiMap<Object, GenericEntity> map;

	public MultiIndex(GenericModelType keyType) {
		this.indexInfo = new IndexInfoImpl();
		this.keyComparator = new NullHandlingComparator<>(SmoodIndexTools.getComparator(keyType));
		this.map = new ComparatorBasedNavigableMultiMap<>(keyComparator, EntityComparator.INSTANCE);
	}

	@Override
	public void addEntity(GenericEntity entity, Object value) {
		map.put(value, entity);
	}

	@Override
	public void removeEntity(GenericEntity entity, Object propertyValue) {
		if (!map.remove(propertyValue, entity))
			throw new IllegalStateException("Entity was not in the index (" + indexInfo.getIndexId() + "), but should have been. Entity: " + entity
					+ ", property value: " + propertyValue);
	}

	@Override
	public void onChangeValue(GenericEntity entity, Object oldValue, Object newValue) {
		if (!VdHolder.isVdHolder(oldValue))
			if (!map.remove(oldValue, entity))
				throw new IllegalStateException("Entity was not in the index (" + indexInfo.getIndexId() + "), but should have been. Entity: "
						+ entity + ", oldValue: " + oldValue + ", newValue: " + newValue);

		map.put(newValue, entity);
	}

	@Override
	protected GenericEntity getThisLevelValue(Object indexValue) {
		return map.get(indexValue);
	}

	@Override
	protected Collection<? extends GenericEntity> getThisLevelValues(Object indexValue) {
		return map.getAll(indexValue);
	}

	@Override
	protected Collection<? extends GenericEntity> allThisLevelValues() {
		return map.values();
	}

	@Override
	public IndexInfoImpl getIndexInfo() {
		return indexInfo;
	}

}
