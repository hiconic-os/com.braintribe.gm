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
 * A set consisting of distinct tuples of other set (operand).
 * 
 * Note that this set is currently only applied after projection, i.e. the corresponding eval implementation is using that number of tuple dimensions.
 * 
 * @author peter.gazdik
 */

public interface DistinctSet extends TupleSet, OperandSet {

	EntityType<DistinctSet> T = EntityTypes.T(DistinctSet.class);

	int getTupleSize();
	void setTupleSize(int tupleSize);

	@Override
	default TupleSetType tupleSetType() {
		return TupleSetType.distinctSet;
	}

}
