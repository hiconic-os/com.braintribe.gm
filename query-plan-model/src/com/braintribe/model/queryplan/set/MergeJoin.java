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

import com.braintribe.model.generic.annotation.Abstract;

/**
 * Represents a cross product of two {@link TupleSet}s with a condition that is resolvable via index (equality, greater/smaller...). The name is wrong
 * though, the implementation does not have to be (and currently isn't) a merge join.
 * 
 * @see MergeLookupJoin
 * @see MergeRangeJoin
 */
@Abstract
public interface MergeJoin extends TupleSet, OperandSet {

	EntityType<MergeJoin> T = EntityTypes.T(MergeJoin.class);

}
