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
package com.braintribe.model.processing.session.impl.persistence;

import com.braintribe.model.access.IncrementalAccess;
import com.braintribe.model.accessapi.ManipulationResponse;
import com.braintribe.model.generic.session.exception.GmSessionException;
import com.braintribe.model.processing.session.api.resource.ResourceAccess;
import com.braintribe.model.query.EntityQueryResult;
import com.braintribe.model.query.PropertyQueryResult;
import com.braintribe.model.query.SelectQueryResult;
import com.braintribe.processing.async.api.AsyncCallback;

public class TransientPersistenceGmSession extends AbstractPersistenceGmSession {

	@Override
	public ResourceAccess resources() {
		throw new UnsupportedOperationException("ResourceAccess not supported by " + getClass().getName());
	}

	@Override
	protected IncrementalAccess getIncrementalAccess() {
		return getBackup();
	}

	@Override
	protected EntityQueryResult postProcess(EntityQueryResult result) throws GmSessionException {
		return result;
	}

	@Override
	protected void postProcess(EntityQueryResult result, AsyncCallback<EntityQueryResult> callback, boolean detached)
			throws GmSessionException {
		callback.onSuccess(result);
	}

	@Override
	protected PropertyQueryResult postProcess(PropertyQueryResult result) throws GmSessionException {
		return result;
	}

	@Override
	protected void postProcess(PropertyQueryResult result, AsyncCallback<PropertyQueryResult> callback, boolean detached)
			throws GmSessionException {
		callback.onSuccess(result);
	}

	@Override
	protected SelectQueryResult postProcess(SelectQueryResult result) throws GmSessionException {
		return result;
	}

	@Override
	protected void postProcess(SelectQueryResult result, AsyncCallback<SelectQueryResult> callback, boolean detached)
			throws GmSessionException {
		callback.onSuccess(result);
	}
	
	@Override
	public ManipulationResponse commit() throws GmSessionException {
		transaction.clear();
		ManipulationResponse response = ManipulationResponse.T.create();
		return response;
	}
	
	@Override
	public void commit(AsyncCallback<ManipulationResponse> callback) {
		callback.onSuccess(commit());
	}
}
