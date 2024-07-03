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
package com.braintribe.model.processing.query.eval.tools;

import java.util.Comparator;

/**
 * 
 * @author peter.gazdik
 */
public class StringAlphabeticalComparator implements Comparator<String> {

	public static final StringAlphabeticalComparator INSTANCE = new StringAlphabeticalComparator();

	private StringAlphabeticalComparator() {
	}

	@Override
	public int compare(String str1, String str2) {
		int res = String.CASE_INSENSITIVE_ORDER.compare(str1, str2);
		if (res == 0) {
			// we want lowerCase to be smaller than upperCase, like SQL DBs usually do (I think)
			res = str2.compareTo(str1);
		}
		return res;
	}

}
