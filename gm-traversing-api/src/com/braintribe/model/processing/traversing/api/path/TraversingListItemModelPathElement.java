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
package com.braintribe.model.processing.traversing.api.path;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.path.api.IListItemModelPathElement;
import com.braintribe.model.generic.path.api.IPropertyRelatedModelPathElement;
import com.braintribe.model.generic.path.api.ModelPathElementType;
import com.braintribe.model.generic.reflection.GenericModelType;

public class TraversingListItemModelPathElement extends TraversingCollectionItemModelPathElement implements IListItemModelPathElement {

	private final int index;

	public TraversingListItemModelPathElement(TraversingModelPathElement previous, Object value, GenericModelType type, int index) {
		super(previous, value, type);
		this.index = index;
	}

	@Override
	public <T extends GenericEntity> T getEntity() {
		TraversingModelPathElement previous = getPrevious();
		if (previous.getElementType() == ModelPathElementType.Property)
			return ((IPropertyRelatedModelPathElement) previous).getEntity();
		else
			return null;
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public ModelPathElementType getElementType() {
		return ModelPathElementType.ListItem;
	}

}
