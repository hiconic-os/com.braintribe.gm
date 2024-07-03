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
package com.braintribe.model.generic.reflection;

import java.util.Collection;
import java.util.Map;

import com.braintribe.common.lcd.UnknownEnumException;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.enhance.EnhancedEntity;
import com.braintribe.model.generic.reflection.CollectionType.CollectionKind;

public class GenericToStringBuilder {
	public static String buildToString(GenericEntity genericEntity, EntityType<?> entityType) {
		if (entityType == null)
			return "";

		StringBuilder builder = new StringBuilder();
		builder.append(entityType.getShortName());

		boolean enhanced = genericEntity instanceof EnhancedEntity;

		builder.append('[');
		builder.append('@');
		builder.append(System.identityHashCode(genericEntity));
		for (Property property : entityType.getProperties()) {
			builder.append(',');
			renderProperty(builder, genericEntity, enhanced, property);
		}
		builder.append(']');

		return builder.toString();
	}

	protected static void renderProperty(StringBuilder builder, GenericEntity genericEntity, boolean enhanced, Property property) {
		if (property.isConfidential())
			return;

		String propertyName = property.getName();

		builder.append(propertyName);
		builder.append('=');

		if (enhanced && property.isAbsent(genericEntity)) {
			builder.append("absent");
			return;
		}

		GenericModelType type = property.getType();
		Object value = property.get(genericEntity);

		if (value == null) {
			builder.append("null");
			return;
		}

		if (type instanceof BaseType)
			type = GMF.getTypeReflection().getType(value);

		if (type instanceof EntityType) {
			EntityType<?> valueEntityType = ((GenericEntity) value).entityType();
			builder.append(valueEntityType.getShortName());

			builder.append('[');
			builder.append('@');
			builder.append(System.identityHashCode(value));
			builder.append(',');
			/* we say false for enhanced (3rd parameter) cause id cannot be absent anyway */
			renderProperty(builder, (GenericEntity) value, false, valueEntityType.getIdProperty());
			builder.append(']');

		} else if (type instanceof EnumType) {
			String prefix = type.getJavaType().getSimpleName();
			builder.append(prefix + "." + value);

		} else if (type instanceof CollectionType) {
			CollectionType collectionType = (CollectionType) type;
			CollectionKind collectionKind = collectionType.getCollectionKind();
			builder.append(collectionKind);
			builder.append("[size=");
			builder.append(size(value, collectionKind));
			builder.append(']');

		} else {
			if (value instanceof String) {
				value = '"' + JavaStringLiteralEscape.escape((String) value) + '"';
			}
			builder.append(value);
		}
	}

	private static int size(Object value, CollectionKind collectionKind) {
		switch (collectionKind) {
			case list:
			case set:
				return ((Collection<?>) value).size();
			case map:
				return ((Map<?, ?>) value).size();
		}

		throw new UnknownEnumException(collectionKind);
	}

}
