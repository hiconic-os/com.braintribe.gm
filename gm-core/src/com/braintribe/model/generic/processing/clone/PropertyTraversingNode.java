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
package com.braintribe.model.generic.processing.clone;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.criteria.CriterionType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;

public class PropertyTraversingNode extends TraversingNode {
	private Property property;
	private GenericEntity entity;
	private Object value;

	
	public PropertyTraversingNode(Property property, GenericEntity entity) {
		super();
		this.property = property;
		this.entity = entity;
		// special assignment to be detected as not resolved
		this.value = this;
	}

	public Property getProperty() {
		return property;
	}
	
	@Override
	public Object getValue() {
		if (value == this) {
			value = property.get(entity);
		}
		return value;
	}
	
	@Override
	public CriterionType getCriterionType() {
		return CriterionType.PROPERTY;
	}
	
	@Override
	public GenericModelType getType() {
		return property.getType();
	}
}
