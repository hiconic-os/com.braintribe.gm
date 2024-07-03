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
package com.braintribe.model.processing.meta.cmd.context.scope;

import static com.braintribe.model.processing.meta.cmd.context.scope.CmdScope.MOMENTARY;
import static com.braintribe.model.processing.meta.cmd.context.scope.CmdScope.SESSION;
import static com.braintribe.model.processing.meta.cmd.context.scope.CmdScope.STATIC;
import static com.braintribe.model.processing.meta.cmd.context.scope.CmdScope.VOLATILE;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScopeComparator {

	/* @formatter:off */
	private static final List<CmdScope> SCOPES_ORDERED_FROM_MOST_STABLE = Arrays.asList(
			STATIC,
			SESSION, 
			MOMENTARY,
			VOLATILE);
	/* @formatter:on */

	private static final Map<CmdScope, Integer> SCOPE_INDEX = new HashMap<CmdScope, Integer>();

	static {
		int i = 0;
		for (CmdScope scopeType: SCOPES_ORDERED_FROM_MOST_STABLE) {
			SCOPE_INDEX.put(scopeType, i++);
		}
	}

	public static CmdScope commonScope(CmdScope cs1, CmdScope cs2) {
		// first make sure the more stable scope is cs1 (i.e. if not, switch them)
		if (compare(cs1, cs2) > 0) {
			CmdScope tmp = cs1;
			cs1 = cs2;
			cs2 = tmp;
		}

		// in this special case, return the scope volatile, otherwise just return the less stable scope
		if (cs1 == SESSION && cs2 == MOMENTARY) {
			return VOLATILE;
		}

		return cs2;
	}

	public static int compare(CmdScope cs1, CmdScope cs2) {
		return SCOPE_INDEX.get(cs1) - SCOPE_INDEX.get(cs2);
	}

}
