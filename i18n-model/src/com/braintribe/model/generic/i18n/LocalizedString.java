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
package com.braintribe.model.generic.i18n;

import java.util.Map;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

import jsinterop.annotations.JsMethod;

public interface LocalizedString extends GenericEntity {

	EntityType<LocalizedString> T = EntityTypes.T(LocalizedString.class);

	String LOCALE_DEFAULT = "default";

	String localizedValues = "localizedValues";

	// @formatter:off
	void setLocalizedValues(Map<String, String> localizedValues);
	Map<String, String> getLocalizedValues();
	// @formatter:on

	@JsMethod
	default LocalizedString put(String locale, String value) {
		getLocalizedValues().put(locale, value);
		return this;
	}
	
	@JsMethod
	default LocalizedString putDefault(String value) {
		getLocalizedValues().put(LOCALE_DEFAULT, value);
		return this;
	}
	
	@JsMethod(name="defaultValue")
	default String value() {
		return value(GMF.getLocale());
	}

	@JsMethod(name="value")
	default String value(String locale) {	
		Map<String, String> map = getLocalizedValues();

		if (map == null) {
			return null;
		}

		while (locale != null) {
			String localizedName = map.get(locale);

			if (localizedName != null)
				return localizedName;

			int index = locale.lastIndexOf('_');
			if (index != -1)
				locale = locale.substring(0, index);
			else
				locale = null;
		}

		return map.get(LOCALE_DEFAULT);
	}
	
	@JsMethod(name = "createLocalizedString", namespace = "hc.i18n")
	static LocalizedString create(String defaultValue) {
		return T.create().putDefault(defaultValue);
	}

}
