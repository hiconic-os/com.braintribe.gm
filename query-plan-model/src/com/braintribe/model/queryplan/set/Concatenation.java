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
package com.braintribe.model.queryplan.set;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * A concatenation of two {@link TupleSet}s. Similar to {@link Union}, but does not check for duplicates, i.e. if some tuple is a member of both
 * operands, then it will be twice in the "set" represented by this entity.
 * <p>
 * Note however, that the intended usage is for cases where both operands are disjoint, in which this is the exact same thing as the aforementioned
 * {@link Union}, but can be implemented without any memory footprint.
 * 
 * <h4>Example:</h4>
 * 
 * <tt>SELECT * FROM A, B, WHERE (a.x > 3) OR (b.x > 3)</tt>
 * 
 * <code>
 * Concatenation {
 * 		firstOperand: CartesianProduct{ FilteredSet;, B} 
 * 		secondOperands: {
 * 			FilteredSet{
 * 				operand: CartesianProduct{ 
 * 							A, 
 * 							FilteredSet;
 * 						}
 * 				filter: NOT(a.x > 3)
 * 			}
 * 		]
 * }
 * </code>
 * 
 * Note that in order for this to work we need to apply the filter for the second set that is the negation of the filter from the first set (thus
 * making sure the two operands of the {@linkplain Concatenation} are disjoint).
 * 
 * 
 */

public interface Concatenation extends CombinedSet {

	EntityType<Concatenation> T = EntityTypes.T(Concatenation.class);

	@Override
	default TupleSetType tupleSetType() {
		return TupleSetType.concatenation;
	}

}
