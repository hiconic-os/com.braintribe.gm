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
package com.braintribe.model.processing.traversing.api.path;

import java.util.Map;

import com.braintribe.model.generic.path.api.IMapKeyModelPathElement;
import com.braintribe.model.generic.path.api.ModelPathElementType;
import com.braintribe.model.generic.reflection.GenericModelType;

public class TraversingMapKeyModelPathElement extends TraversingCollectionItemModelPathElement implements IMapKeyModelPathElement {

	private final Object mapValue;
	private final GenericModelType mapValueType;
	private final Map.Entry<?, ?> entry;

	public TraversingMapKeyModelPathElement(TraversingModelPathElement previous, Object value, GenericModelType type, Object mapValue,
			GenericModelType mapValueType, Map.Entry<?, ?> entry) {
		super(previous, value, type);
		this.mapValue = mapValue;
		this.mapValueType = mapValueType;
		this.entry = entry;
	}

	@Override
	public ModelPathElementType getElementType() {
		return ModelPathElementType.MapKey;
	}

	@Override
	public <T extends GenericModelType> T getKeyType() {
		return getType();
	}

	@Override
	public <T> T getKey() {
		return getValue();
	}

	@Override
	public <T extends GenericModelType> T getMapValueType() {
		return mapValueType.cast();
	}

	@Override
	public <T> T getMapValue() {
		return (T) mapValue;
	}

	@Override
	public <K, V> Map.Entry<K, V> getMapEntry() {
		return (Map.Entry<K, V>) entry;
	}
}
