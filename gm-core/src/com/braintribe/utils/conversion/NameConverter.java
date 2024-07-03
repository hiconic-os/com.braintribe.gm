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
package com.braintribe.utils.conversion;

import com.braintribe.model.meta.data.display.NameConversionStyle;
import com.braintribe.utils.lcd.StringTools;

/**
 * @author peter.gazdik
 */
public class NameConverter {

	public static String convert(String camelCase, NameConversionStyle style) {
		switch (style) {
			case screamingSnakeCase:
				return StringTools.camelCaseToScreamingSnakeCase(camelCase);
			case snakeCase:
				return StringTools.camelCaseToSnakeCase(camelCase);
			default:
				throw new UnsupportedOperationException("This converter does not support the style: " + style);

		}
	}

}
