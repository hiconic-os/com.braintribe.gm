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
package com.braintribe.model.processing.query.tools;

import java.util.Comparator;

public class ScalarComparator implements Comparator<Object> {

	public static final ScalarComparator INSTANCE = new ScalarComparator();

	@Override
	public int compare(Object o1, Object o2) {
		Class<?> c1 = o1.getClass();
		Class<?> c2 = o2.getClass();

		if (c1 == c2) {
			return ((Comparable<Object>) o1).compareTo(o2);
		}

		// We try to compare simple names first, as the difference should be detected faster, ignoring the common
		// java(.lang) prefix, for most cases.
		int result = c1.getSimpleName().compareTo(c2.getSimpleName());
		if (result != 0) {
			return result;
		}

		//
		return c1.getName().compareTo(c2.getName());
	}

}
