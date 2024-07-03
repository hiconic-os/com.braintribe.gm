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
package com.braintribe.model.processing.itw.synthesis.gm.experts.template;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.TypeCode;
import com.braintribe.model.processing.itw.synthesis.gm.experts.PropertyPathResolver;

public class VariableTemplatePart extends TemplatePart {
	private final String[] encodedPropertyPath;
	private Property propertyPath[];

	public VariableTemplatePart(String[] encodedPropertyPath) {
		this.encodedPropertyPath = encodedPropertyPath;
	}

	@Override
	public void append(StringBuilder builder, GenericEntity entity, EntityType<?> entityType) {
		if (propertyPath == null) {
			Property propertyPath[] = new Property[encodedPropertyPath.length];

			for (int i = 0; i < encodedPropertyPath.length; i++) {
				String propertyName = encodedPropertyPath[i];

				Property property = entityType.getProperty(propertyName);

				propertyPath[i] = property;
				GenericModelType type = property.getType();

				if (type.getTypeCode() == TypeCode.entityType) {
					entityType = type.cast();
				} else {
					entityType = null;
				}
			}

			this.propertyPath = propertyPath;
		}

		builder.append(PropertyPathResolver.resolvePropertyPath(entity, propertyPath));
	}
}
