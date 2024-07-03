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
package com.braintribe.model.queryplan.index;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.queryplan.set.OperandSet;
import com.braintribe.model.queryplan.set.TupleSet;
import com.braintribe.model.queryplan.value.Value;

/**
 * NOTE: Implements OperandSet (which exists just for convenience) even though it is not a {@link TupleSet}. Really, the only point of
 * {@link OperandSet} is as a handle for something that has property operand of type {@link TupleSet}.
 * 
 * @see Index
 */

public interface GeneratedIndex extends Index, OperandSet {

	EntityType<GeneratedIndex> T = EntityTypes.T(GeneratedIndex.class);

	Value getIndexKey();
	void setIndexKey(Value indexKey);

	@Override
	default IndexType indexType() {
		return IndexType.generated;
	}

}
