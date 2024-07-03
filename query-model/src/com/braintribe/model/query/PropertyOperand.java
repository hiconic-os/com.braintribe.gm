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
package com.braintribe.model.query;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.Property;

public interface PropertyOperand extends Operand {

	EntityType<PropertyOperand> T = EntityTypes.T(PropertyOperand.class);

	/**
	 * The property path for given source. Right, the "path", because it doesn't have to be only one property, but a
	 * path in the form: {@code prop1.prop2. ... .propN}
	 */
	String getPropertyName();
	void setPropertyName(String propertyName);

	void setSource(Source source);
	Source getSource();

	static PropertyOperand create(String propertyName) {
		return create(null, propertyName);
	}

	static PropertyOperand create(Property property) {
		return create(property.getName());
	}
	
	static PropertyOperand create(Source source, String propertyName) {
		PropertyOperand operand = PropertyOperand.T.create();
		operand.setPropertyName(propertyName);
		operand.setSource(source);
		return operand;
	}
	
	static PropertyOperand create(Source source, Property property) {
		return create(source, property.getName());
	}
}
