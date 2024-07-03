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

import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * 
 * Base class for some set operations on tuples.
 * <p>
 * NOTE: Operands must be compatible, i.e. the structure of those tuple sets is the same. By structure we mean the underlying source sets and joins.
 * So for example one can combine two {@link SourceSet}s iff it is the exact same source.
 * 
 * @see Concatenation
 * @see Union
 * @see Intersection
 * 
 * @author pit & dirk
 */
@Abstract
public interface CombinedSet extends TupleSet {

	EntityType<CombinedSet> T = EntityTypes.T(CombinedSet.class);

	TupleSet getFirstOperand();
	void setFirstOperand(TupleSet firstOperand);

	TupleSet getSecondOperand();
	void setSecondOperand(TupleSet secondOperand);

	int getTupleSize();
	void setTupleSize(int tupleSize);

}
