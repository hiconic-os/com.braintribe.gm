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
package com.braintribe.model.processing.session.impl.managed;

import com.braintribe.model.access.IncrementalAccess;
import com.braintribe.model.access.ModelAccessException;
import com.braintribe.model.generic.session.exception.GmSessionException;
import com.braintribe.model.processing.session.api.managed.SelectQueryExecution;
import com.braintribe.model.processing.session.api.managed.SelectQueryResultConvenience;
import com.braintribe.model.query.SelectQuery;
import com.braintribe.model.query.SelectQueryResult;
import com.braintribe.processing.async.api.AsyncCallback;

class BasicSelectQueryExecution extends AbstractQueryResultConvenience<SelectQuery, SelectQueryResult, SelectQueryResultConvenience> implements SelectQueryExecution {
	private IncrementalAccess access;
	
	public BasicSelectQueryExecution(IncrementalAccess access, SelectQuery selectQuery) {
		super(selectQuery);
		this.access = access;
	}

	@Override
	protected SelectQueryResult resultInternal(SelectQuery query) throws GmSessionException {
		try {
			return access.query(query);
		} catch (ModelAccessException e) {
			throw new GmSessionException("error while executing entity query", e);
		}
	}
	
	@Override
	public void result(AsyncCallback<SelectQueryResultConvenience> callback) {
		try {
			callback.onSuccess(new StaticSelectQueryResultConvenience(getQuery(), result()));
		}
		catch (Throwable t) {
			callback.onFailure(t);
		}
	}		
	

}
