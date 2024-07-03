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
import com.braintribe.model.processing.session.api.managed.QueryExecution;
import com.braintribe.model.processing.session.api.managed.QueryResultConvenience;
import com.braintribe.model.query.Query;
import com.braintribe.processing.async.api.AsyncCallback;

class BasicQueryExecution extends AbstractDelegatingQueryResultConvenience implements QueryExecution {
	private Query query;
	
	public BasicQueryExecution(IncrementalAccess access, Query query) {
		super(access, query);
		this.query = query;
	}

	@Override
	public void result(AsyncCallback<QueryResultConvenience> callback) {
		try {
			callback.onSuccess(new StaticQueryResultConvenience(this.query,result()));
		}
		catch (Throwable t) {
			callback.onFailure(t);
		}
	}

	
}
