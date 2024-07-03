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

import com.braintribe.model.generic.path.api.IMapValueModelPathElement;
import com.braintribe.model.generic.path.api.ModelPathElementType;
import com.braintribe.model.generic.reflection.GenericModelType;

/**
 * This {@link TraversingPropertyRelatedModelPathElement} is special as it adds another value aspect (besides {@link #getValue()}) which is the
 * {@link #getKey() key} of the corresponding map entry.
 * 
 * @author dirk.scheffler
 * @author pit.steinlin
 * @author peter.gazdik
 */
public class TraversingMapValueModelPathElement extends TraversingCollectionItemModelPathElement implements IMapValueModelPathElement {

	private final TraversingMapKeyModelPathElement keyElement;

	public TraversingMapValueModelPathElement(TraversingMapKeyModelPathElement keyElement) {
		super(keyElement.getPrevious(), keyElement.getMapValue(), keyElement.getMapValueType());
		this.keyElement = keyElement;
	}

	@Override
	public TraversingMapKeyModelPathElement getKeyElement() {
		return keyElement;
	}

	@Override
	public <T> T getKey() {
		return keyElement.getKey();
	}

	@Override
	public <T extends GenericModelType> T getKeyType() {
		return keyElement.getKeyType();
	}

	@Override
	public ModelPathElementType getElementType() {
		return ModelPathElementType.MapValue;
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
	public <K, V> Map.Entry<K, V> getMapEntry() {
		return keyElement.getMapEntry();
	}

}
