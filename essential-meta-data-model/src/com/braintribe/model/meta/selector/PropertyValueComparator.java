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
package com.braintribe.model.meta.selector;

import com.braintribe.model.generic.annotation.meta.Mandatory;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface PropertyValueComparator extends MetaDataSelector {

	EntityType<PropertyValueComparator> T = EntityTypes.T(PropertyValueComparator.class);

	@Mandatory
	Operator getOperator();
	void setOperator(Operator operator);

	/**
	 * If null the current context property will be taken (if available).
	 */
	String getPropertyPath();
	void setPropertyPath(String propertyPath);

	Object getValue();
	void setValue(Object value);
	
	default PropertyValueComparator initPropertyValueComparator(String propertyPath, Operator operator, Object value) {
		setOperator(operator);
		setPropertyPath(propertyPath);
		setValue(value);
		
		return this;
	}
	
	static PropertyValueComparator create(String propertyPath, Operator operator, Object value) {
		return T.create().initPropertyValueComparator(propertyPath, operator, value);
	}

}
