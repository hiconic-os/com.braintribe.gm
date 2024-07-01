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
package com.braintribe.model.processing.session.impl.managed;

import java.util.List;

import com.braintribe.model.access.IncrementalAccess;
import com.braintribe.model.generic.pr.criteria.TraversingCriterion;
import com.braintribe.model.generic.session.exception.GmSessionException;
import com.braintribe.model.generic.session.exception.GmSessionRuntimeException;
import com.braintribe.model.processing.session.api.managed.QueryResultConvenience;
import com.braintribe.model.query.EntityQuery;
import com.braintribe.model.query.PropertyQuery;
import com.braintribe.model.query.Query;
import com.braintribe.model.query.QueryResult;
import com.braintribe.model.query.SelectQuery;

public class AbstractDelegatingQueryResultConvenience implements QueryResultConvenience {
	protected QueryResultConvenience delegate;
	
	public AbstractDelegatingQueryResultConvenience(IncrementalAccess access, Query query) {
		this.delegate = buildDelegate(access, query);
	}
	
	public AbstractDelegatingQueryResultConvenience(QueryResultConvenience delegate) {
		this.delegate = delegate;
	}

	protected QueryResultConvenience buildDelegate(IncrementalAccess access, Query query) {
		if (query instanceof EntityQuery) {
			return new BasicEntityQueryExecution(access, (EntityQuery)query);
		} else if (query instanceof PropertyQuery) {
			return new BasicPropertyQueryExecution(access, (PropertyQuery)query);
		} else if (query instanceof SelectQuery) {
			return new BasicSelectQueryExecution(access, (SelectQuery)query);
		} 
		throw new GmSessionRuntimeException("Unsupported query type: "+query);
	}

	@Override
	public QueryResult result() throws GmSessionException {
		return delegate.result();
	}

	@Override
	public <E> List<E> list() throws GmSessionException {
		return delegate.list();
	}

	@Override
	public <E> E first() throws GmSessionException {
		return delegate.first();
	}

	@Override
	public <E> E unique() throws GmSessionException {
		return delegate.unique();
	}

	@Override
	public <E> E value() throws GmSessionException {
		return delegate.value();
	}

	@Override
	public QueryResultConvenience setVariable(String name, Object value) {
		return delegate.setVariable(name, value);
	}

	@Override
	public QueryResultConvenience setTraversingCriterion(
			TraversingCriterion traversingCriterion) {
		return delegate.setTraversingCriterion(traversingCriterion);
	}

	
	
}
