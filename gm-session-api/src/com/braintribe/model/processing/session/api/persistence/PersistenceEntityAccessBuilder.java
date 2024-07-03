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
package com.braintribe.model.processing.session.api.persistence;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.criteria.TraversingCriterion;
import com.braintribe.model.generic.session.exception.GmSessionException;
import com.braintribe.model.processing.session.api.managed.EntityAccessBuilder;
import com.braintribe.model.processing.session.api.managed.NotFoundException;
import com.braintribe.processing.async.api.AsyncCallback;

public interface PersistenceEntityAccessBuilder<T extends GenericEntity> extends EntityAccessBuilder<T> {
	public PersistenceEntityAccessBuilder<T> withTraversingCriterion(TraversingCriterion tc);
	
	/**
	 * Returns the specified entity or <code>null</code>, if it doesn't exist. Attention: if the entity is already available in the session,
	 * it will just be returned, i.e. no query will be executed and thus properties will not be updated! This also
	 * means that the {@link #withTraversingCriterion(TraversingCriterion) traversing criterion} (if set) will not be applied!
	 * 
	 * @see PersistenceEntityAccessBuilder#refresh()
	 */
	@Override
	public T find() throws GmSessionException;
	
	/**
	 * Returns the specified entity if it is already available in the session, or creates a new shallow instance
	 * otherwise. Note that this method always returns and entity and never performs any query.
	 * 
	 * @see PersistenceGmSession#shallowifyInstances()
	 */
	public T findLocalOrBuildShallow() throws GmSessionException;

	/**
	 * Refreshes the specified entity. This method works like {@link #require()}, but it always executes a query, 
	 * i.e. even if the entity is already available in the session.
	 */
	public T refresh() throws GmSessionException, NotFoundException;
	
	public void refresh(AsyncCallback<T> asyncCallback);
}
