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

import static com.braintribe.model.processing.smood.population.SmoodIndexTools.getComparator;

import java.util.Comparator;
import java.util.NavigableMap;
import java.util.TreeMap;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.GenericModelType;

/**
 * 
 */
public class UniqueMetricIndex extends UniqueIndex implements SmoodMetricIndex {

	private final NavigableMap<Object, GenericEntity> navigableMap;
	private final Comparator<Object> keyComparator;

	public UniqueMetricIndex(GenericModelType keyType) {
		this(getComparator(keyType));
	}

	protected UniqueMetricIndex(Comparator<Object> keyComparator) {
		super(new TreeMap<>(keyComparator));

		this.keyComparator = keyComparator;
		this.navigableMap = (NavigableMap<Object, GenericEntity>) map;
	}

	@Override
	public Comparator<Object> getKeyComparator() {
		return keyComparator;
	}

	@Override
	public NavigableMap<Object, GenericEntity> getNavigableMap() {
		return navigableMap;
	}

}
