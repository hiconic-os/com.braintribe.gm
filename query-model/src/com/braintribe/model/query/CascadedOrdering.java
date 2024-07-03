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

import java.util.List;


import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * An {@link Ordering} that contains a list of orderings and can be used to set an order on more than one column. This works in a
 * simple hierarchy. The first property defined in the first instance is ordered first, the second is ordered within this order, and so on.
 */

public interface CascadedOrdering extends Ordering {

	EntityType<CascadedOrdering> T = EntityTypes.T(CascadedOrdering.class);

	void setOrderings(List<SimpleOrdering> orderings);
	List<SimpleOrdering> getOrderings();

}
