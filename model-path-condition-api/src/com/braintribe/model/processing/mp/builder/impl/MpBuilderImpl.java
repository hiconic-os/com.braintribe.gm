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
package com.braintribe.model.processing.mp.builder.impl;

import java.util.List;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.path.ListItemPathElement;
import com.braintribe.model.generic.path.ModelPathElement;
import com.braintribe.model.generic.path.PropertyPathElement;
import com.braintribe.model.generic.path.RootPathElement;
import com.braintribe.model.generic.path.api.IModelPathElement;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.GenericModelTypeReflection;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.processing.mp.builder.api.MpBuilder;

public class MpBuilderImpl implements MpBuilder {

	private static final GenericModelTypeReflection typeReflection = GMF.getTypeReflection();
	
	private ModelPathElement currentElement;
	private GenericEntity lastEntity;
	private Property lastProperty;
	private Object lastValue;
	private EntityType<?> lastEntityType;

	@Override
	public MpBuilder root(Object root) {
		currentElement = new RootPathElement(getType(root), root);
		updateLastValue(root);
		return this;
	}

	@Override
	public MpBuilder listItem(int listIndex) {
		List<?> list = (List<?>) lastValue;		
		Object listItem = list.get(listIndex);
		ListItemPathElement element = new ListItemPathElement(lastEntity, lastProperty, listIndex, getType(listItem), listItem);
		
		append(element, listItem);
		
		return this;
	}

	@Override
	public MpBuilder property(String propertyName) {
		lastProperty = lastEntityType.getProperty(propertyName);
		Object propertyValue = lastProperty.get(lastEntity);
		PropertyPathElement element = new PropertyPathElement(lastEntity, lastProperty, propertyValue);

		append(element, propertyValue);

		return this;
	}

	@Override
	public IModelPathElement build() {
		return currentElement;
	}

	//
	// Helper methods
	//

	private void append(ModelPathElement element, Object value) {
		currentElement.append(element);
		currentElement = element;

		updateLastValue(value);
	}

	private void updateLastValue(Object value) {
		if (value instanceof GenericEntity) {
			lastEntity = (GenericEntity) value;
			lastEntityType = typeReflection.getEntityType(lastEntity);
		}
		
		lastValue = value;
	}

	private static GenericModelType getType(Object root) {
		return typeReflection.getType(root);
	}
	
}
