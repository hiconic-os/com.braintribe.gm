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
 * Represent a full <a href="https://en.wikipedia.org/wiki/Cartesian_product">Cartesian product</a> of the passed tuple sets.
 * 
 * <h4>Example:</h4>
 * 
 * <tt>select * from Person p, Document d </tt>
 * 
 * <code>
 * CartesianProduct {
 * 		operands: [
 * 			SourceSet ;, 
 * 			SourceSet ;
 * 		]
 * }
 * </code>
 */
public interface CartesianProduct extends TupleSet {

	EntityType<CartesianProduct> T = EntityTypes.T(CartesianProduct.class);

	List<TupleSet> getOperands();
	void setOperands(List<TupleSet> operands);

	@Override
	default TupleSetType tupleSetType() {
		return TupleSetType.cartesianProduct;
	}

}
