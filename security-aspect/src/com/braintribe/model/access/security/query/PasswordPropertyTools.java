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
package com.braintribe.model.access.security.query;

import com.braintribe.logging.Logger;
import com.braintribe.model.access.security.query.QueryOperandTools.EntityTypeProperty;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.TypeCode;
import com.braintribe.model.meta.data.prompt.Confidential;
import com.braintribe.model.processing.meta.cmd.CascadingMetaDataException;
import com.braintribe.model.processing.meta.cmd.builders.EntityMdResolver;
import com.braintribe.model.processing.meta.cmd.builders.ModelMdResolver;

/**
 * 
 */
class PasswordPropertyTools {

	public static final String HIDDEN_PASSWORD = "*****";

	private static final Logger log = Logger.getLogger(PasswordPropertyTools.class);

	public static Object getValueToReplacePassword(Property passwordProperty) {
		return getValueToReplacePassword(passwordProperty.getType());
	}

	public static Object getValueToReplacePassword(GenericModelType pawwordPropertyType) {
		if (pawwordPropertyType.getTypeCode() == TypeCode.stringType) {
			return HIDDEN_PASSWORD;

		} else {
			return null;
		}
	}

	public static boolean isPasswordProperty(EntityTypeProperty etp, ModelMdResolver mdResolver) {
		return isPasswordProperty(etp.entityType, etp.propertyName, mdResolver);
	}

	public static boolean isPasswordProperty(EntityType<?> et, String property, ModelMdResolver mdResolver) {
		return isPasswordProperty(property, mdResolver.entityType(et));
	}

	private static boolean isPasswordProperty(String property, EntityMdResolver mdResolver) {
		try {
			return mdResolver.property(property).is(Confidential.T);

		} catch (CascadingMetaDataException e) {
			log.warn("Error while resolving PasswordProperty meta data. ", e);

			return false;
		}
	}

}
