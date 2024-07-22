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
package com.braintribe.model.bvd.query;

import java.util.List;
import com.braintribe.model.generic.base.EnumBase;
import com.braintribe.model.generic.reflection.EnumType;
import com.braintribe.model.generic.reflection.EnumTypes;

public enum ResultConvenience implements EnumBase<ResultConvenience> {
	
	/**
	 * Returns the envelope object (an instance of com.braintribe.model.query.QueryResult returned by the query.
	 */
	result, 
	/**
	 * Returns the query result as {@link List} 
	 */
	list,
	/**
	 * Convenience to return the first instance of the query result, or <code>null</code> if the query returns not results. 
	 */
	first,
	/**
	 * Convenience method to return a single instance of the query result, or <code>null</code> if the query returns no results.
	 * This method throws an Exception if more then one result is returned by the query. 
	 */
	unique, 
	/**
	 * Returns the actual query result value. Depending on the used query type the type of the returned value could vary from
	 * List (e.g.: for EntityQueries) to the according type of the requested property in a SelectQuery or PropertyQuery.   
	 */
	value;

	public static final EnumType<ResultConvenience> T = EnumTypes.T(ResultConvenience.class);
	
	@Override
	public EnumType<ResultConvenience> type() {
		return T;
	}	
	
}
