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
package com.braintribe.model.processing.validation.expert;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * Utility class that hosts methods commonly being used by the validation tasks.
 * </p>
 * 
 *
 */
public final class CommonChecks {

	private CommonChecks() {
	}

	public static boolean isNotNull(Object object) {
		return object != null;
	}

	public static boolean areValuesUnique(List<String> values) {
		Set<String> set = new HashSet<>();
		for (String value : values) {
			if (!set.add(value))
				return false;
		}
		return true;
	}
}
