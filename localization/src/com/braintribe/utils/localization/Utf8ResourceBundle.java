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
package com.braintribe.utils.localization;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * This makes sure that we can read UTF8 property files.
 */
public abstract class Utf8ResourceBundle {

	protected static Utf8Control utf8Control = new Utf8Control(); 

	public static ResourceBundle getBundle(String baseName, Locale locale, ClassLoader loader) {
		return ResourceBundle.getBundle(baseName, locale, loader, utf8Control);
	}

	public static ResourceBundle getBundle(URL url, Locale locale, ClassLoader loader) {
		String baseName = url.getPath();
		// Removing .properties from the name
		baseName = baseName.replace(".properties", "");
		return ResourceBundle.getBundle(baseName, locale, loader, utf8Control);
	}

}
