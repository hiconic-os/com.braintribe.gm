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
 * This entity is meant to represent the SELECT clause and is very similar to {@link Projection}. The difference is this gives information that
 * aggregate functions are being used, which means a completely different implementation of the projection must be used (this one has to store the
 * entire result set in the memory).
 * 
 * <h4>Example:</h4>
 * 
 * <tt>select p.name, count(p.id) from Person p</tt>
 * 
 * <code>
 * AggregatingProjection {
 * 		operand: SourceSet;
 * 		values:[
 * 			ValueProperty{
 * 				value: TupleComponent{tupleComponentPosition: SourceSet;},
 * 				propertyPath: "name"
 * 			},
 * 			AggregateFunction {
 * 	 			operand: ValueProperty{
 * 					value: TupleComponent{tupleComponentPosition: SourceSet;},
 * 					propertyPath: "id"
 * 				}
 * 				aggregateType: Aggregation
 * 			}
 * 		]
 * }
 * </code>
 */

public interface AggregatingProjection extends Projection {

	EntityType<AggregatingProjection> T = EntityTypes.T(AggregatingProjection.class);

	// TODO note this is optional, only if it also was in the query

	List<Value> getGroupByValues();
	void setGroupByValues(List<Value> groupByValues);

	@Override
	default TupleSetType tupleSetType() {
		return TupleSetType.aggregatingProjection;
	}

}
