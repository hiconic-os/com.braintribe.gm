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
package com.braintribe.model.processing.query.eval.context;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.i18n.LocalizedString;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.GenericModelTypeReflection;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.processing.query.eval.api.QueryEvaluationContext;

/**
 * 
 */
class FulltextComparator {

	private static final GenericModelTypeReflection typeReflection = GMF.getTypeReflection();
	private static final EntityType<?> LOCALIZED_STRING_TYPE = typeReflection.getEntityType(LocalizedString.class);

	static boolean matches(GenericEntity entity, String text, QueryEvaluationContext context) {
		text = text.toLowerCase();

		EntityType<GenericEntity> entityType = entity.entityType();

		for (Property property: entityType.getProperties()) {
			String value = getStringValue(entity, property, context);

			if (value != null && value.toLowerCase().contains(text))
				return true;
		}

		return false;
	}

	private static String getStringValue(GenericEntity entity, Property property, QueryEvaluationContext context) {
		GenericModelType propertyType = property.getType();

		switch (propertyType.getTypeCode()) {
			case booleanType:
			case dateType:
			case decimalType:
			case doubleType:
			case enumType:
			case floatType:
			case integerType:
			case longType:
				return safeToString(property.get(entity));
			case stringType:
				return (String) property.get(entity);
			case entityType: {
				if (LOCALIZED_STRING_TYPE.isAssignableFrom(propertyType))
					return context.resolveLocalizedString((LocalizedString) property.get(entity));
				return null;
			}
			case objectType: {
				if (property.isIdentifier())
					return safeToString(property.get(entity));
				return null;
			}
			default:
				return null;
		}
	}

	private static String safeToString(Object o) {
		return o == null ? null : o.toString();
	}
}
