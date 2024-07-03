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
package com.braintribe.model.pagination;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.meta.Description;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * An entity that expresses the limit and offset properties when applying pagination.
 * <p>
 * For example, if we want to get 10 results skipping the first 30 (i.e. elements 31st-40th) we would specify {@code limit = 10} and
 * {@code offset = 30};
 * <p>
 * If we want all the elements, we use {@code limit = offset = 0}.
 * 
 * @author peter.gazdik
 */
public interface HasPagination extends GenericEntity {

	EntityType<HasPagination> T = EntityTypes.T(HasPagination.class);

	@Description("Specifies the maximum number of elements returned. Value 0 means there is no limit.")
	int getPageLimit();
	void setPageLimit(int pageLimit);

	@Description("Specifies how many elements should be skipped from the original list. Value 0 means start from the beginning.")
	int getPageOffset();
	void setPageOffset(int pageOffset);

	default boolean hasPagination() {
		return getPageLimit() > 0 || getPageOffset() > 0;
	}

}
