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
 * This entity is meant to represent the SELECT clause. The operand represents the result of the query if we were selecting everything ( "select *
 * from ..."). If however there are some attributes or functions specified for the SELECT clause, we represent each of such attributes as an element
 * in the "values" list. Functions (not supported yet) could be something like type-conversion, substring or even aggregate functions like count(*).
 * 
 * <h4>Example:</h4>
 * 
 * <tt>select p, c.owner, 5 from Person p, Company c where p.companyName = c.name</tt>
 * 
 * <code>
 * Projection {
 * 		operand: CartesianProduct;
 * 		values:[
 * 			TupleComponent{tupleComponentPosition: SourceSet;},
 * 			ValueProperty{
 * 				value: TupleComponent{tupleComponentPosition: SourceSet;},
 * 				propertyPath: "owner"
 * 			},
  			StaticValue ;
 * 		]
 * }
 * </code>
 */

public interface Projection extends TupleSet {

	EntityType<Projection> T = EntityTypes.T(Projection.class);

	TupleSet getOperand();
	void setOperand(TupleSet operand);

	List<Value> getValues();
	void setValues(List<Value> values);

	@Override
	default TupleSetType tupleSetType() {
		return TupleSetType.projection;
	}

}
