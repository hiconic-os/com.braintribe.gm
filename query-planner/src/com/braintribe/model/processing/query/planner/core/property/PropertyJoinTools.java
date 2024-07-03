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
package com.braintribe.model.processing.query.planner.core.property;

import static com.braintribe.utils.lcd.CollectionTools2.newList;
import static com.braintribe.utils.lcd.CollectionTools2.newSet;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.braintribe.model.query.Join;
import com.braintribe.model.query.Source;
import com.braintribe.utils.lcd.CollectionTools2;

/**
 * @author peter.gazdik
 */
public class PropertyJoinTools {

	public static Set<Join> leafJoins(Set<Join> joins) {
		Set<Join> nonLeafs = newSet();

		for (Join join: joins) {
			nonLeafs.addAll(allJoinsOnChain(join.getSource()));
		}

		return CollectionTools2.substract(joins, nonLeafs);
	}

	private static Collection<Join> allJoinsOnChain(Source chainStart) {
		List<Join> result = newList();

		while (chainStart instanceof Join) {
			Join join = (Join) chainStart;
			result.add(join);
			chainStart = join.getSource();
		}

		return result;
	}

	public static List<Join> relativeJoinChain(Set<Join> sourceJoins, Join targetJoin) {
		List<Join> result = newList();

		do {
			if (sourceJoins.contains(targetJoin)) {
				targetJoin = null;

			} else {
				result.add(targetJoin);

				Source source = targetJoin.getSource();
				targetJoin = source instanceof Join ? (Join) source : null;
			}

		} while (targetJoin != null);

		Collections.reverse(result);

		return result;
	}

}
