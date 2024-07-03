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



/**
 * @see Index
 */

public interface RepositoryIndex extends Index {

	EntityType<RepositoryIndex> T = EntityTypes.T(RepositoryIndex.class);

	String getIndexId();
	void setIndexId(String indexId);

	/**
	 * This is redundant, but makes evaluator implementation easier, because the implementation returns tuples, so we need the component
	 * position to generate the right tuple.
	 */
	int getTupleComponentIndex();
	void setTupleComponentIndex(int tupleComponentIndex);

	@Override
	default IndexType indexType() {
		return IndexType.repository;
	}

}
