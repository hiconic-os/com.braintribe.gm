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

import java.util.List;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * Abstract base type for any result of a computation where {@link HasPagination pagination} was applied, typically given by a request which extends
 * {@link HasPagination}. One could say this instance represents a single page in this context.
 * <p>
 * The actual type must contain exactly one property of type {@link List}, whose elements represent the desired subsequence based on given
 * {@link HasPagination#getPageLimit() limit} and {@link HasPagination#getPageOffset() offset} parameters. If there was a need for more than one List
 * properties on the result, all other lists should be embedded in another entity type which will then be referenced by the result entity type.
 * 
 * @author peter.gazdik
 */
@Abstract
public interface Paginated extends GenericEntity {

	EntityType<Paginated> T = EntityTypes.T(Paginated.class);

	/**
	 * Value <tt>true</tt> means there are more elements in the original result, while <tt>false</tt> means this response already contains the last
	 * element.
	 */
	boolean getHasMore();
	void setHasMore(boolean hasMore);

}
