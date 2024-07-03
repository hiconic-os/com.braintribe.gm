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
package com.braintribe.model.access.impl;

import java.util.Set;

import com.braintribe.model.access.AccessService;
import com.braintribe.model.access.IncrementalAccess;
import com.braintribe.model.accessapi.ManipulationRequest;
import com.braintribe.model.accessapi.ManipulationResponse;
import com.braintribe.model.accessapi.ReferencesRequest;
import com.braintribe.model.accessapi.ReferencesResponse;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.query.EntityQuery;
import com.braintribe.model.query.EntityQueryResult;
import com.braintribe.model.query.PropertyQuery;
import com.braintribe.model.query.PropertyQueryResult;
import com.braintribe.model.query.SelectQuery;
import com.braintribe.model.query.SelectQueryResult;

/**
 * An implementation of {@link IncrementalAccess} that wraps an instance of {@link AccessService} and delegates all calls to the service with the
 * access ID configured via {@link #setAccessId(String)}.
 * 
 */
public class AccessServiceDelegatingAccess implements IncrementalAccess {

	private AccessService accessService;
	private String accessId;

	public AccessService getAccessService() {
		return accessService;
	}

	public void setAccessService(AccessService accessService) {
		this.accessService = accessService;
	}

	public void setAccessId(String accessId) {
		this.accessId = accessId;
	}

	@Override
	public String getAccessId() {
		return accessId;
	}

	@Override
	public GmMetaModel getMetaModel() {
		return accessService.getMetaModel(accessId);
	}

	@Override
	public SelectQueryResult query(SelectQuery query) {
		return accessService.query(accessId, query);
	}

	@Override
	public EntityQueryResult queryEntities(EntityQuery request) {
		return accessService.queryEntities(accessId, request);
	}

	@Override
	public PropertyQueryResult queryProperty(PropertyQuery request) {
		return accessService.queryProperty(accessId, request);
	}

	@Override
	public ManipulationResponse applyManipulation(ManipulationRequest manipulationRequest) {
		return accessService.applyManipulation(accessId, manipulationRequest);
	}

	@Override
	public ReferencesResponse getReferences(ReferencesRequest referencesRequest) {
		return accessService.getReferences(accessId, referencesRequest);
	}

	@Override
	public Set<String> getPartitions() {
		return accessService.getPartitions(accessId);
	}

}
