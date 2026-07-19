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

import com.braintribe.model.generic.GMF;

/**
 * @author peter.gazdik
 */
public class EnumTypes {

	// See GM_INITIALIZATION

	// DO NOT EVEN THINK ABOUT UNCOMMENTING THIS!!!!
	// private static GenericModelTypeReflection typeReflection = GMF.getTypeReflection();

	public static <E extends Enum<E>> EnumType<E> T(Class<E> clazz) {
		if (!GM_INITIALIZATION.T_LITERAL_INIT_ENABLED)
			return null;

		// DO NOT EXTRACT TO A FILED!!!
		return GMF.getTypeReflection().getEnumTypeSafe(clazz);
	}

}
