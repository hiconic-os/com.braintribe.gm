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
package com.braintribe.model.processing.itw.synthesis.gm.experts;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.i18n.LocalizedString;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.session.GmSession;
import com.braintribe.utils.i18n.I18nTools;

public class PropertyPathResolver {

	public static String getLocale(GenericEntity entity) {
		GmSession gmSession = entity.session();
		if (gmSession != null) {
			// return gmSession.getLocale;
			// TODO this should be finished once GmSession supports the 'getLocale()' method.
		}

		return GMF.getLocale();
	}

	public static Object resolvePropertyPath(GenericEntity entity, Property propertyChain[]) {
		Object value = entity;
		for (int i = 0; value != null && i < propertyChain.length; i++) {
			value = propertyChain[i].get((GenericEntity) value);
		}

		if (value instanceof LocalizedString) {

			String locale = getLocale(entity);
			LocalizedString ls = (LocalizedString) value;
			return I18nTools.get(ls, locale);
		} else
			return value != null ? value : "";
	}
}
