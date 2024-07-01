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
package com.braintribe.model.queryplan.set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.queryplan.set.join.Join;

/**
 * Represents a (partial) result of a query. If you think of a query result as a 2 dimensional matrix (array), then one tuple is a row in such matrix
 * and a "TupleSet" represents actually this entire matrix.
 * <p>
 * On a side note: Tuple actually means something like an array or a list. If you have 3 elements it's "triple", 4 - "quadruple", 5 - "quintuple",...
 * , n - "n-tuple". In general, if we do not want to specify the size, we just say "tuple". Simple as that.
 * <p>
 * From a one table perspective, a tuple is identical to a table row, but the term row is not very accurate when talking about a combination of data
 * from various tables (when doing joins/Cartesian product), so the tuple is the more general expression for this.
 * 
 * <h4>Why tuple sets when there are no tuples?</h4>
 * 
 * This entities only represent the plan for a query, they do not contain any data. See examples below.
 * 
 * @see SourceSet
 * @see IndexSet
 * @see StaticSet
 * @see FilteredSet
 * @see Union
 * @see Intersection
 * @see Join
 * @see CartesianProduct
 */
@Abstract
public interface TupleSet extends GenericEntity {

	EntityType<TupleSet> T = EntityTypes.T(TupleSet.class);

	TupleSetType tupleSetType();

}
