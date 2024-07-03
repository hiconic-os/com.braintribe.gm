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
package com.braintribe.model.query;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * This class encapsulates the paging related properties of the returned results. One can restrict the amount of the returned results
 * {@link #setPageSize(int)} and set from which index value the results should be displayed {@link #setStartIndex(int)}.
 */
public interface Paging extends GenericEntity {

	EntityType<Paging> T = EntityTypes.T(Paging.class);

	/** Maximum number of rows returned. Zero and negative values are interpreted as positive infinity (well, Integer.MAX_VALUE). */
	int getPageSize();
	void setPageSize(int pageSize);

	int getStartIndex();
	void setStartIndex(int page);

	static Paging create(int startIndex, int pageSize) {
		Paging paging = Paging.T.create();
		paging.setStartIndex(startIndex);
		paging.setPageSize(pageSize);
		return paging;
	}

}
