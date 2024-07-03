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
package com.braintribe.model.generic.path;

import java.util.Map.Entry;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.path.api.IMapKeyModelPathElement;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;

@SuppressWarnings("unusable-by-js")
public class MapKeyPathElement extends PropertyRelatedModelPathElement implements IMapKeyModelPathElement {

	private final Object entryValue;
	private final GenericModelType entryValueType;

	public MapKeyPathElement(GenericEntity entity, Property property, GenericModelType keyType, Object key, GenericModelType type, Object value) {
		super(entity, property, keyType, key);
		this.entryValueType = type;
		this.entryValue = value;
	}

	@Override
	public ModelPathElementType getPathElementType() {
		return ModelPathElementType.MapKey;
	}

	@Override
	public MapKeyPathElement copy() {
		return new MapKeyPathElement(getEntity(), getProperty(), getType(), getValue(), entryValueType, entryValue);
	}

	@Override
	public com.braintribe.model.generic.path.api.ModelPathElementType getElementType() {
		return com.braintribe.model.generic.path.api.ModelPathElementType.MapKey;
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
		return (T) entryValueType;
	}

	@Override
	public <T> T getMapValue() {
		return (T) entryValue;
	}

	@Override
	public <K, V> Entry<K, V> getMapEntry() {
		return new ModelPathMapEntry<>((K) getValue(), (V) entryValue);
	}
}
