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

/**
 * This entity is meant to represent the ORDER BY clause.
 * 
 * <h4>Example:</h4>
 * 
 * <tt>select * from Person p order by p.lastName, p.firstName</tt>
 * 
 * <code>
 * OrderedSet {
 * 		operand: SourceSet;
 * 		sortCriteria:[
 * 			SortCriterion{
 * 				ValueProperty{TupleComponent{SourceSet;}, lastName}
 * 			}
 * 			SortCriterion{
 * 				ValueProperty{TupleComponent{SourceSet;}, firstName}
 * 			}
 * 		]
 * }
 * </code>
 */

public interface OrderedSet extends TupleSet {

	EntityType<OrderedSet> T = EntityTypes.T(OrderedSet.class);

	TupleSet getOperand();
	void setOperand(TupleSet operand);

	List<SortCriterion> getSortCriteria();
	void setSortCriteria(List<SortCriterion> sortCriteria);

	@Override
	default TupleSetType tupleSetType() {
		return TupleSetType.orderedSet;
	}

}
