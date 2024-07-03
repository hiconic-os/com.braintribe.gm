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
package com.braintribe.model.queryplan.value;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.queryplan.set.TupleSet;

/**
 * This is a very special value, because it references a {@link TupleSet}. Basically it represents a (hash) set of all the values described
 * by {@link HashSetProjection#setValue(Value)} in given {@linkplain TupleSet}. This may for instance be used to enable usage of nested
 * queries as operands in the where clause, so for example a query like:
 * <code>select * from Person p where p.company in (select c from Company c where [someCondition]) </code>, would turn into a plan like
 * this:
 * 
 * <code> 
 * FilteredSet {
 * 		operand: SourceSet ;*
 * 		filter: In {
 * 			leftOperand: TupleComponent {
 * 					tupleComponentIndex: 0
 * 				}
 * 			rightOperand: HashSetProjection {
 * 				tupleSet: FilteredSet {Company;*, [someCondition],}
 * 				value: TupleComponent(tupleComponentIndex:0)
 * 			} 
 * 		}
 * }
 * * - note that both sources have index 0, as they come from different source (which is currently not supported) 
 * </code>
 */

public interface HashSetProjection extends Value {

	EntityType<HashSetProjection> T = EntityTypes.T(HashSetProjection.class);

	TupleSet getTupleSet();
	void setTupleSet(TupleSet tupleSet);

	Value getValue();
	void setValue(Value value);

	@Override
	default ValueType valueType() {
		return ValueType.hashSetProjection;
	}

}
