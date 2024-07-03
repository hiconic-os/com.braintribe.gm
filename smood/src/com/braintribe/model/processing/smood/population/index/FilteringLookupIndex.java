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
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.processing.smood.population.info.IndexInfoImpl;

/**
 * We use this for globalId index for a concrete type - i.e. only instances of that type pass the filter.
 * 
 * @author peter.gazdik
 */
public class FilteringLookupIndex implements LookupIndex {

	private final LookupIndex delegate;
	private final Predicate<? super GenericEntity> filter;
	protected final IndexInfoImpl indexInfo;

	public FilteringLookupIndex(LookupIndex delegate, Predicate<GenericEntity> filter) {
		this.delegate = delegate;
		this.filter = filter;
		this.indexInfo = new IndexInfoImpl();
	}

	@Override
	public void addEntity(GenericEntity entity, Object value) {
		delegate.addEntity(entity, value);
	}

	@Override
	public void removeEntity(GenericEntity entity, Object propertyValue) {
		delegate.removeEntity(entity, propertyValue);
	}

	@Override
	public void onChangeValue(GenericEntity entity, Object oldValue, Object newValue) {
		delegate.onChangeValue(entity, oldValue, newValue);
	}

	@Override
	public <T extends GenericEntity> T getValue(Object indexValue) {
		T result = delegate.getValue(indexValue);
		return filter.test(result) ? result : null;
	}

	@Override
	public Collection<? extends GenericEntity> getValues(Object indexValue) {
		return delegate.getValues(indexValue).stream() //
				.filter(filter) //
				.collect(Collectors.toList());
	}

	@Override
	public Collection<? extends GenericEntity> allValues() {
		return delegate.allValues().stream() //
				.filter(filter) //
				.collect(Collectors.toList());
	}

	@Override
	public IndexInfoImpl getIndexInfo() {
		return indexInfo;
	}

}
