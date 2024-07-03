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

/**
 * Represents the limit and offset part of the query.
 * 
 * <h4>Example:</h4>
 * 
 * <tt>select from Person p limit 10 offset 20</tt>
 * 
 * <code>
 * PagintatedSet {
 * 		operand: SourceSet;
 * 		limit: 10
 * 		offset: 20
 * }
 * </code>
 */

public interface PaginatedSet extends TupleSet {

	EntityType<PaginatedSet> T = EntityTypes.T(PaginatedSet.class);

	TupleSet getOperand();
	void setOperand(TupleSet operand);

	int getLimit();
	void setLimit(int limit);

	int getOffset();
	void setOffset(int offset);

	int getTupleSize();
	void setTupleSize(int tupleSize);

	/**
	 * We had a bug with initial implementation of how hasMore is resolved for {@link PaginatedSet}. Initially, there was only a check whether the
	 * operand iterator has more results (hasNext is still true after getting all the needed tuples). However, in case our operand is for example
	 * concatenation of two different query results (happens in smart access), these results might have pagination already applied. If one of the
	 * query then returns required number of results and the other returns zero, then the delegate iterator has no next, and we would evaluate to
	 * false, but the thing is, the first operand might have more results (i.e. the query result might have had the flag set to true). Therefore, we
	 * have added this flag that says the operand might already have pagination applied, so if no next entry exists, it checks whether the operand
	 * itself says it "has more" (if the flag is set).
	 */
	boolean getOperandMayApplyPagination();
	void setOperandMayApplyPagination(boolean operandMayApplyPagination);

	@Override
	default TupleSetType tupleSetType() {
		return TupleSetType.pagination;
	}

}
