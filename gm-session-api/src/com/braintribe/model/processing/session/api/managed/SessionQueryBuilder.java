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

import static java.util.Objects.requireNonNull;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.session.exception.GmSessionException;
import com.braintribe.model.generic.value.EntityReference;
import com.braintribe.model.query.EntityQuery;
import com.braintribe.model.query.PropertyQuery;
import com.braintribe.model.query.Query;
import com.braintribe.model.query.SelectQuery;

public interface SessionQueryBuilder {
	
	/**
	 * Creates a {@link SelectQueryExecution} that can be used to execute the passed {@link SelectQuery} instance.
	 */
	SelectQueryExecution select(SelectQuery selectQuery);
	
	/**
	 * Creates a {@link SelectQueryExecution} that can be used to execute the {@link SelectQuery} represented by the passed selectQueryString.
	 */
	SelectQueryExecution select(String selectQueryString);
	
	/**
	 * Creates a {@link EntityQueryExecution} that can be used to execute the passed {@link EntityQuery} instance.
	 */
	EntityQueryExecution entities(EntityQuery entityQuery);
	
	/**
	 * Creates a {@link EntityQueryExecution} that can be used to execute the {@link EntityQuery} represented by the passed entityQueryString.
	 */
	EntityQueryExecution entities(String entityQueryString);
	
	/**
	 * Creates a {@link PropertyQueryExecution} that can be used to execute the passed {@link PropertyQuery} instance.
	 */
	PropertyQueryExecution property(PropertyQuery propertyQuery);

	/**
	 * Creates a {@link PropertyQueryExecution} that can be used to execute the {@link PropertyQuery} represented by the passed propertyQueryString.
	 */
	PropertyQueryExecution property(String propertyQueryString);
	
	/**
	 * Creates a {@link QueryExecution} that can be used to executed the passed {@link Query} instance.<br/>  
	 * This method provides the option to generically execute any of the three supported derivations of abstract {@link Query}.
	 * 
	 * <li> {@link EntityQuery} </li>
	 * <li> {@link PropertyQuery} </li>
	 * <li> {@link SelectQuery} </li>
	 * 
	 * <br/>
	 */
	QueryExecution abstractQuery(Query query);
	
	/**
	 * Creates a {@link QueryExecution} that can be used to execute the {@link Query} represented by the queryString.<br/> 
	 * This method provides the option to generically execute any of the three supported derivations of abstract {@link Query}.
	 * 
	 * <li> {@link EntityQuery} </li>
	 * <li> {@link PropertyQuery} </li>
	 * <li> {@link SelectQuery} </li>
	 * 
	 * <br/>
	 */
	QueryExecution abstractQuery(String queryString);
	
	default <T extends GenericEntity> T getEntity(String globalId) {
		return requireNonNull(findEntity(globalId), () -> "Entity not found for globalId: " + globalId);
	}

	/**
	 * @returns entity with given globalId or <tt>null</tt> if no entity found.
	 *
	 * @see #getEntity(String)
	 */
	<T extends GenericEntity> T findEntity(String globalId) throws GmSessionException;

	/**
	 * Works like {@link #entity(EntityReference)}.
	 */
	<T extends GenericEntity> EntityAccessBuilder<T> entity(String typeSignature, Object id);
	
	<T extends GenericEntity> EntityAccessBuilder<T> entity(String typeSignature, Object id, String partition);

	/**
	 * Creates an {@link EntityAccessBuilder} which can be used to query/find the entity specified by the passed
	 * <code>entityReference</code>.
	 */
	<T extends GenericEntity> EntityAccessBuilder<T> entity(EntityReference entityReference);

	/**
	 * Works like {@link #entity(EntityReference)}.
	 */
	<T extends GenericEntity> EntityAccessBuilder<T> entity(EntityType<T> entityType, Object id);
	
	<T extends GenericEntity> EntityAccessBuilder<T> entity(EntityType<T> entityType, Object id, String partition);

	/**
	 * Gets the {@link EntityReference} for the specified <code>entity</code> and then works like
	 * {@link #entity(EntityReference)}.
	 */
	<T extends GenericEntity> EntityAccessBuilder<T> entity(T entity);	
}
