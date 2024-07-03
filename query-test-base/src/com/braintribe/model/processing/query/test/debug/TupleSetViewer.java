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
package com.braintribe.model.processing.query.test.debug;

import com.braintribe.model.processing.query.tools.QueryPlanPrinter;
import com.braintribe.model.queryplan.set.TupleSet;

/**
 * textual visualization tool for tuple sets
 * 
 * @author pit
 * 
 */
public class TupleSetViewer {

	private static final int TEST_TITLE_WIDTH = 100;

	public static void view(String testName, TupleSet set) {
		printTestName(testName);
		System.out.println(QueryPlanPrinter.print(set));
		printTestBottomBorder();
	}

	protected static void printTestName(String testName) {
		int len = Math.max(TEST_TITLE_WIDTH - 2 - testName.length(), 20);
		String s = chain((len + 1) / 2, "*") + " " + testName + " " + chain(len / 2, "*");
		System.out.println(s);
	}

	protected static void printTestBottomBorder() {
		System.out.println(chain(TEST_TITLE_WIDTH, "*") + "\n");
	}

	protected static String chain(int count, String s) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < count; i++)
			result.append(s);

		return result.toString();
	}
}
