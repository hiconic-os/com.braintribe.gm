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
package com.braintribe.gm.service.access;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang.NotImplementedException;

import com.braintribe.model.access.AccessService;
import com.braintribe.model.access.AccessServiceException;
import com.braintribe.model.access.IncrementalAccess;
import com.braintribe.model.accessapi.AccessDomain;
import com.braintribe.model.accessapi.ManipulationRequest;
import com.braintribe.model.accessapi.ManipulationResponse;
import com.braintribe.model.accessapi.ModelEnvironment;
import com.braintribe.model.accessapi.ModelEnvironmentServices;
import com.braintribe.model.accessapi.ReferencesRequest;
import com.braintribe.model.accessapi.ReferencesResponse;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.query.EntityQuery;
import com.braintribe.model.query.EntityQueryResult;
import com.braintribe.model.query.PropertyQuery;
import com.braintribe.model.query.PropertyQueryResult;
import com.braintribe.model.query.SelectQuery;
import com.braintribe.model.query.SelectQueryResult;

public class SimpleAccessService implements AccessService {
	
	private final Map<String, IncrementalAccess> accesses = new HashMap<>();
	
	public void reset(IncrementalAccess access) {
		accesses.put(access.getAccessId(), access);
	}

	public void addAccess(IncrementalAccess access){
		accesses.put(access.getAccessId(), access);
	}

	@Override
	public ModelEnvironment getModelEnvironment(String accessId) throws AccessServiceException {
		throw new NotImplementedException();
	}

	@Override
	public ModelEnvironment getModelAndWorkbenchEnvironment(String accessId) throws AccessServiceException {
		throw new NotImplementedException();
	}

	@Override
	public ModelEnvironment getModelAndWorkbenchEnvironment(String accessId, Set<String> workbenchPerspectiveNames) throws AccessServiceException {
		throw new NotImplementedException();
	}

	@Override
	public ModelEnvironment getModelEnvironmentForDomain(String accessId, AccessDomain accessDomain) throws AccessServiceException {
		throw new NotImplementedException();
	}

	@Override
	public ModelEnvironmentServices getModelEnvironmentServices(String accessId) throws AccessServiceException {
		throw new NotImplementedException();
	}

	@Override
	public ModelEnvironmentServices getModelEnvironmentServicesForDomain(String accessId, AccessDomain accessDomain) throws AccessServiceException {
		throw new NotImplementedException();
	}

	@Override
	public GmMetaModel getMetaModelForTypes(Set<String> typeSignatures) throws AccessServiceException {
		throw new NotImplementedException();
	}
	
	@Override
	public Set<String> getAccessIds() throws AccessServiceException {
		return accesses.keySet();
	}
	
	@Override
	public Set<String> getPartitions(String accessId) throws AccessServiceException {
		return requireAccess(accessId).getPartitions();
	}

	@Override
	public GmMetaModel getMetaModel(String accessId) throws AccessServiceException {
		return requireAccess(accessId).getMetaModel();
	}

	@Override
	public SelectQueryResult query(String accessId, SelectQuery query) throws AccessServiceException {
		return requireAccess(accessId).query(query);
	}

	@Override
	public PropertyQueryResult queryProperty(String accessId, PropertyQuery request) throws AccessServiceException {
		return requireAccess(accessId).queryProperty(request);
	}

	@Override
	public EntityQueryResult queryEntities(String accessId, EntityQuery request) throws AccessServiceException {
		return requireAccess(accessId).queryEntities(request);
	}

	@Override
	public ManipulationResponse applyManipulation(String accessId, ManipulationRequest manipulationRequest) throws AccessServiceException {
		return requireAccess(accessId).applyManipulation(manipulationRequest);
	}

	@Override
	public ReferencesResponse getReferences(String accessId, ReferencesRequest referencesRequest) throws AccessServiceException {
		return requireAccess(accessId).getReferences(referencesRequest);
	}

	private IncrementalAccess requireAccess(String accessId) {
		return Objects.requireNonNull(accesses.get(accessId), "Could not find access with id '" + accessId + "'.");
	}
}
