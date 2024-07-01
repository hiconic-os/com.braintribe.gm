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
package com.braintribe.model.processing.query.planner.core.cross;

import java.util.List;
import java.util.Set;

import com.braintribe.model.processing.query.planner.context.OrderedSourceDescriptor;
import com.braintribe.model.query.From;
import com.braintribe.model.queryplan.set.TupleSet;

/**
 * 
 */
public class FromGroup {

	public final TupleSet tupleSet;
	public final Set<From> froms;
	public final List<OrderedSourceDescriptor> osds;
	public final int osdIndex;

	/**
	 * This <tt>id</tt> is used to identify a use-case when joining two FromGroups together, or a FromGroup and an index. The point is, every
	 * condition that joins the same pair of such objects must be identified as same use-case, so we can take advantage of taking more conditions at
	 * once.
	 * <p>
	 * Using IDs is better than creating a wrapper with special equals/hashCode methods.
	 */
	public final int id;

	private static int ID_COUNTER = 0;

	public FromGroup(TupleSet tupleSet, Set<From> froms, List<OrderedSourceDescriptor> osds) {
		this.tupleSet = tupleSet;
		this.froms = froms;
		this.osds = osds;
		this.osdIndex = minIndex(osds);
		this.id = ID_COUNTER++;
	}

	private static int minIndex(List<OrderedSourceDescriptor> osds) {
		return osds.stream().mapToInt(osd -> osd.index).min().orElse(Integer.MAX_VALUE);
	}

}
