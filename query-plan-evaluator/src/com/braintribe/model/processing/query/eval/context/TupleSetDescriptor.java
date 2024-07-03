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
package com.braintribe.model.processing.query.eval.context;

import java.util.Map;

import com.braintribe.model.processing.query.eval.set.join.AbstractEvalPropertyJoin;
import com.braintribe.model.queryplan.TupleComponentPosition;
import com.braintribe.model.queryplan.set.join.JoinedMapKey;
import com.braintribe.model.queryplan.set.join.MapJoin;

/**
 * 
 */
public interface TupleSetDescriptor {

	/**
	 * TODO this might not be needed. The context only needs this for the purpose of resolving value type, which is only
	 * needed for a right join, but maybe could be done in a different way. See comment at
	 * {@link AbstractEvalPropertyJoin}.
	 */
	Map<Integer, TupleComponentPosition> getComponentPositionMapping();

	Map<JoinedMapKey, MapJoin> getMapJoinMapping();

	int fullProductComponentsCount();

	int resultComponentsCount();

}
