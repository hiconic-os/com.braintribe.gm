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
package com.braintribe.model.processing.session.api.managed;

import java.util.List;

import com.braintribe.model.generic.pr.criteria.TraversingCriterion;
import com.braintribe.model.generic.session.exception.GmSessionException;
import com.braintribe.model.query.EntityQuery;
import com.braintribe.model.query.PropertyQuery;
import com.braintribe.model.query.Query;
import com.braintribe.model.query.QueryResult;
import com.braintribe.model.query.SelectQuery;

public interface QueryResultConvenience {
	
	/**
	 * Returns the envelope object (an instance of {@link QueryResult}) returned by the query.
	 * Concrete derivations of {@link QueryResultConvenience} may override this method and specify a concrete implementation of {@link QueryResult}
	 */
	QueryResult result() throws GmSessionException;
	
	/**
	 * Returns the query result as {@link List} 
	 */
	<E> List<E> list() throws GmSessionException;
	
	/**
	 * Convenience method to return the first instance of the query result, or <code>null</code> if the query returns not results. 
	 */
	<E> E first() throws GmSessionException;
	
	/**
	 * Convenience method to return a single instance of the query result, or <code>null</code> if the query returns no results.
	 * This method throws an {@link GmSessionException} if more then one result is returned by the query. 
	 */
	<E> E unique() throws GmSessionException;

	/**
	 * Returns the actual query result value. Depending on the used query type the type of the returned value could vary from
	 * {@link List} (e.g.: for {@link EntityQuery}s) to the according type of the requested property in a {@link SelectQuery} or {@link PropertyQuery}.   
	 */
	<E> E value() throws GmSessionException;
	
	/**
	 * Fills the variable context for the query evaluation.
	 */
	QueryResultConvenience setVariable(String name, Object value);
	
	/**
	 * Sets the passed traversingCriterion to the {@link Query}. An already existing {@link TraversingCriterion} will be overridden by this method.
	 */
	QueryResultConvenience setTraversingCriterion(TraversingCriterion traversingCriterion);

}
