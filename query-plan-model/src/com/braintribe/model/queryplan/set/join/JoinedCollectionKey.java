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
package com.braintribe.model.queryplan.set.join;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;


import com.braintribe.model.queryplan.TupleComponentPosition;

/**
 * A key that identifies an entry in a joined collection (in case the joined property type is List or Map). One can view a {@link ListJoin}
 * and a {@link MapJoin} as a join which adds two new components, the index for each element of the collection, and the collection value
 * itself. For better understanding see example below.
 * 
 * <tt>select * from Person p join p.children c_index, c where c_index < 4</tt>
 * 
 * <code>
 * 
 * 
 * FilteredSet {
 * 		operand: ListJoin {
 * 		  	operand: SourceSet* ;
 * 			listIndex: JoinedListIndex** {
 * 				index: 1
 * 			} 
 * 		  	index: 2
 * 		  	tupleComponentPosition: SourceSet* ;
 * 		  	propertyPath: "children"
 * 	  	}
 * 		filter: LessThan {
 * 			leftOperand: TupleComponent {
 * 				tupleComponentPosition: JoinedListIndex** {
 * 					index: 1
 * 				}
 * 				tupleSlot: SourceSet* ;
 * 				propertyPath: "name"
 * 			}
 * 			rightOperand: StaticValue ; 
 * 		}
 * }
 * * - same instance 
 * ** - same instance
 * }
 * </code>
 * 
 * @see ListJoin
 * @see JoinedListIndex
 * 
 * @see MapJoin
 * @see JoinedMapKey
 * 
 * 
 * 
 */

public interface JoinedCollectionKey extends TupleComponentPosition {

	EntityType<JoinedCollectionKey> T = EntityTypes.T(JoinedCollectionKey.class);

}
