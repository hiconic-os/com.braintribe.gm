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
import com.braintribe.model.queryplan.filter.Condition;

/**
 * Filters a set by iterating over each element in the operand and filtering it by applying the filter to each element in turn.
 * 
 * <h4>Example:</h4>
 * 
 * <tt>select * from Person p where p.name = "John"</tt>
 * 
 * <code>
 * FilteredSet {
 * 		operand: SourceSet ;*
 * 		filter: Equality {
 * 			leftOperand: ValueProperty {
 * 				value: TupleComponent {
 * 					tupleComponentPosition: SourceSet ;*
 * 				}
 * 				propertyPath: "name"
 * 			}
 * 			rightOperand: StaticValue {value: "John"} 
 * 		}
 * }
 * * - same instance 
 * </code>
 * 
 * @author pit & dirk
 */

public interface FilteredSet extends TupleSet, OperandSet {

	EntityType<FilteredSet> T = EntityTypes.T(FilteredSet.class);

	Condition getFilter();
	void setFilter(Condition filter);

	@Override
	default TupleSetType tupleSetType() {
		return TupleSetType.filteredSet;
	}

}
