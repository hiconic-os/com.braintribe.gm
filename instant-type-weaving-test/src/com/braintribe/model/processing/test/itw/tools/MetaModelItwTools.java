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
package com.braintribe.model.processing.test.itw.tools;

import static com.braintribe.model.generic.builder.meta.MetaModelBuilder.entityType;
import static com.braintribe.model.generic.builder.meta.MetaModelBuilder.property;
import static com.braintribe.utils.lcd.CollectionTools2.newList;

import java.util.Arrays;
import java.util.List;

import com.braintribe.model.generic.builder.meta.MetaModelBuilder;
import com.braintribe.model.meta.GmEntityType;
import com.braintribe.model.meta.GmEnumConstant;
import com.braintribe.model.meta.GmEnumType;
import com.braintribe.model.meta.GmProperty;
import com.braintribe.model.meta.GmType;

/**
 * @author peter.gazdik
 */
public class MetaModelItwTools {

	public static GmEntityType newGmEntityType(String typeSignature, GmEntityType superType) {
		GmEntityType result = entityType(typeSignature, Arrays.asList(superType));
		result.setIsAbstract(false);
		return result;
	}

	public static GmProperty addProperty(GmEntityType entityType, String name, GmType type) {
		GmProperty p = property(entityType, name, type);

		List<GmProperty> properties = entityType.getProperties();
		if (properties == null)
			entityType.setProperties(properties = newList());

		properties.add(p);

		return p;
	}

	public static GmEnumType enumType(String typeSignature) {
		return MetaModelBuilder.enumType(typeSignature);
	}

	public static GmEnumConstant addConstant(GmEnumType enumType, String name) {
		GmEnumConstant c = enumConstant(enumType, name);

		List<GmEnumConstant> constants = enumType.getConstants();
		if (constants == null)
			enumType.setConstants(constants = newList());

		constants.add(c);

		return c;
	}

	public static GmEnumConstant enumConstant(GmEnumType enumType, String name) {
		return MetaModelBuilder.enumConstant(enumType, name);
	}

}
