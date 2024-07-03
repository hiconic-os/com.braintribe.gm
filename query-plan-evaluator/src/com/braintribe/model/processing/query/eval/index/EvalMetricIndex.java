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
package com.braintribe.model.processing.query.eval.index;

import com.braintribe.model.processing.query.eval.api.Tuple;

/**
 * 
 */
public interface EvalMetricIndex extends EvalIndex {

	/** Returns entities by given bounds */
	Iterable<Tuple> getIndexRange(Object from, Boolean fromInclusive, Object to, Boolean toInclusive);

	/**
	 * Returns all entities from the index in normal or reversed order. This is useful when doing a query with ordering by an indexed property (by a
	 * metric index of course).
	 */
	Iterable<Tuple> getFullRange(boolean reverseOrder);

}
