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
package com.braintribe.model.access;

import java.util.Set;

import com.braintribe.cfg.Configurable;
import com.braintribe.model.accessapi.CustomPersistenceRequest;
import com.braintribe.model.accessapi.ManipulationRequest;
import com.braintribe.model.accessapi.ManipulationResponse;
import com.braintribe.model.accessapi.ReferencesRequest;
import com.braintribe.model.accessapi.ReferencesResponse;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.processing.service.api.ServiceRequestContext;
import com.braintribe.model.query.EntityQuery;
import com.braintribe.model.query.EntityQueryResult;
import com.braintribe.model.query.PropertyQuery;
import com.braintribe.model.query.PropertyQueryResult;
import com.braintribe.model.query.SelectQuery;
import com.braintribe.model.query.SelectQueryResult;

public abstract class AbstractDynamicDelegatingAccess implements IncrementalAccess {

	protected String accessId = null;
	
	@Override
	public GmMetaModel getMetaModel() {
		return getDelegate().getMetaModel();
	}
	
	@Override
	public ManipulationResponse applyManipulation(ManipulationRequest manipulationRequest)
			throws ModelAccessException {
		return getDelegate().applyManipulation(manipulationRequest);
	}

	@Override
	public EntityQueryResult queryEntities(EntityQuery request) throws ModelAccessException {
		return getDelegate().queryEntities(request);
	}

	@Override
	public PropertyQueryResult queryProperty(PropertyQuery request) throws ModelAccessException {
		return getDelegate().queryProperty(request);
	}
	
	@Override
	public SelectQueryResult query(SelectQuery query) throws ModelAccessException {
		return getDelegate().query(query);
	}

	@Override
	public ReferencesResponse getReferences(ReferencesRequest referencesRequest)
			throws ModelAccessException {
		return getDelegate().getReferences(referencesRequest);
	}
	
	@Override
	public String getAccessId() {
		if (this.accessId != null) {
			return this.accessId;
		}
		return getDelegate().getAccessId();
	}
	
	@Override
	public Set<String> getPartitions() throws ModelAccessException {
		return getDelegate().getPartitions();
	}

	@Override
	public Object processCustomRequest(ServiceRequestContext context, CustomPersistenceRequest request) {
		return getDelegate().processCustomRequest(context, request);
	}
	
	abstract protected IncrementalAccess getDelegate();

	@Configurable
	public void setAccessId(String accessId) {
		this.accessId = accessId;
	}

}
