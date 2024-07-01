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
package com.braintribe.model.processing.validation.expert.property;

import java.lang.reflect.Method;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.processing.PropertyValidationContext;
import com.braintribe.model.processing.PropertyValidationExpert;
import com.braintribe.utils.lcd.StringTools;

public class DeprecatedAnnotationExpert implements PropertyValidationExpert {

	@Override
	public void validate(PropertyValidationContext context) {
		Property property = context.getProperty();
		GenericEntity entity = context.getEntity();
		if (!property.isEmptyValue(context.getPropertyValue())) {
			Class<?> entityClass = entity.entityType().getJavaType();
			String propertyGetterName = "get" + StringTools.capitalize(property.getName());
			
			try {
				Method getterMethod = entityClass.getMethod(propertyGetterName);
				if (getterMethod.getAnnotation(Deprecated.class) != null) {
					context.notifyConstraintViolation("Value of deprecated property was set.");
				}
			} catch (NoSuchMethodException e) {
				throw new RuntimeException("Property '" + property.getName() + "' does not have a getter method '" + propertyGetterName + "()' in class " + entityClass);
			} 
		}
	}

}
