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
package com.braintribe.model.generic.path;

import java.util.Map.Entry;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.path.api.IMapValueModelPathElement;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;




@SuppressWarnings("unusable-by-js")
public class MapValuePathElement extends PropertyRelatedModelPathElement implements IMapValueModelPathElement {
	private final Object key;
	private final GenericModelType keyType;
	private final MapKeyPathElement keyElement;

	// TODO validate that adding keyElement is ok with all invokers of this constructor
	public MapValuePathElement(GenericEntity entity, Property property, GenericModelType keyType, Object key, GenericModelType type, Object value, MapKeyPathElement keyElement) {
		super(entity, property, type, value);
		this.keyType = keyType;
		this.key = key;
		this.keyElement = keyElement;
	}

	@Override
	public <T extends GenericModelType> T getKeyType() {
		return (T) keyType;
	}

	@Override
	public <T> T getKey() {
		return (T) key;
	}

	@Override
	public ModelPathElementType getPathElementType() {
		return ModelPathElementType.MapValue;
	}

	@Override
	public MapValuePathElement copy() {
		return new MapValuePathElement(getEntity(), getProperty(), getKeyType(), getKey(), getType(), getValue(), getKeyElement());
	}

	@Override
	public <T extends GenericModelType> T getMapValueType() {
		return getType();
	}

	@Override
	public <T> T getMapValue() {
		return getValue();
	}

	@Override
	public <K, V> Entry<K, V> getMapEntry() {
		return new ModelPathMapEntry<>((K) key, (V) getValue());
	}

	@Override
	public com.braintribe.model.generic.path.api.ModelPathElementType getElementType() {
		return com.braintribe.model.generic.path.api.ModelPathElementType.MapValue;
	}

	@Override
	public MapKeyPathElement getKeyElement() {
		return keyElement;
	}

}
