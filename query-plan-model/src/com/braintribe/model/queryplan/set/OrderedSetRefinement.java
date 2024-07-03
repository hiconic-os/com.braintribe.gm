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

import java.util.List;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.queryplan.value.Value;

/**
 * This is something that can be used together with {@link IndexOrderedSet} to represent the ORDER BY clause, as an alternative to just
 * {@link OrderedSet}.
 * <p>
 * In some cases we might use an index to provide our source in correct order. But we might have additional sorting criteria, for which we need to
 * sort the tuples where the primary sorting values are equal.
 * 
 * <h4>Example:</h4>
 * 
 * <tt>select p from Person p order by p.indexedLastName, p.firstName</tt>
 * 
 * <code>
 * OrderedSetRefinement {
 * 		operand: IndexOrderedSet* {
 * 			typeSignature: "Person"
 * 			propertyName: indexedLastName
 * 			metricIndex: RepositoryMetricIndex {indexId: "person#indexedLastName"}
 * 			descending: false
 * 		}
 * 		
 * 		groupValues: [
 * 			Value{TupleComponent{IndexOrderedSet;}, lastName}
 * 		]
 * 		sortCriteria:[
 * 			SortCriterion{
 * 				ValueProperty{TupleComponent{IndexOrderedSet;}, firstName}
 * 			}
 * 		]
 * }
 * </code>
 */

public interface OrderedSetRefinement extends OrderedSet {

	EntityType<OrderedSetRefinement> T = EntityTypes.T(OrderedSetRefinement.class);

	/**
	 * List of values which the tuples we get from the underlying {@link #getOperand() operand} is already sorted by. This values thus define groups
	 * of tuples, where tuples in a common group have all these values the same (see example in the class description).
	 */
	List<Value> getGroupValues();
	void setGroupValues(List<Value> groupValues);

	@Override
	default TupleSetType tupleSetType() {
		return TupleSetType.orderedSetRefinement;
	}

}
