// ============================================================================
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
// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
// ============================================================================
package com.braintribe.model.query;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * An {@link Ordering} that can be used to set an order on one property only. The results are ordered according to a
 * property {@link #setOrderBy(Object)} in the defined order direction {@link #setDirection(OrderingDirection)}
 */
public interface SimpleOrdering extends Ordering {

	EntityType<SimpleOrdering> T = EntityTypes.T(SimpleOrdering.class);

	/** The property on which the ordering should take place */
	Object getOrderBy();
	void setOrderBy(Object orderBy);

	/** The {@link OrderingDirection} in which the results should be ordered */
	OrderingDirection getDirection();
	void setDirection(OrderingDirection direction);

	static SimpleOrdering create(OrderingDirection direction, Object orderValue) {
		SimpleOrdering ordering = SimpleOrdering.T.create();
		ordering.setDirection(direction);
		ordering.setOrderBy(orderValue);
		return ordering;
	}
	
	static SimpleOrdering create(Object orderValue) {
		return create(OrderingDirection.ascending, orderValue);
	}
}
