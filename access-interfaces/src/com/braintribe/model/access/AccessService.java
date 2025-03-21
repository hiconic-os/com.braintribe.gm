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

import com.braintribe.model.accessapi.AccessDomain;
import com.braintribe.model.accessapi.ManipulationRequest;
import com.braintribe.model.accessapi.ManipulationResponse;
import com.braintribe.model.accessapi.ModelEnvironment;
import com.braintribe.model.accessapi.ModelEnvironmentServices;
import com.braintribe.model.accessapi.ReferencesRequest;
import com.braintribe.model.accessapi.ReferencesResponse;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.query.EntityQuery;
import com.braintribe.model.query.EntityQueryResult;
import com.braintribe.model.query.PropertyQuery;
import com.braintribe.model.query.PropertyQueryResult;
import com.braintribe.model.query.SelectQuery;
import com.braintribe.model.query.SelectQueryResult;

/**
 * An {@link IncrementalAccess} delegate service.
 * 
 * @author dirk.scheffler
 * @author gunther.schenk
 */
public interface AccessService {

	/** Returns the {@link ModelEnvironment} for the access identified by <code>accessId</code>. */
	ModelEnvironment getModelEnvironment(String accessId) throws AccessServiceException;

	/** Returns the {@link ModelEnvironment} for the access identified by <code>accessId</code>. */
	ModelEnvironment getModelAndWorkbenchEnvironment(String accessId) throws AccessServiceException;

	/** Returns the {@link ModelEnvironment} for the access identified by <code>accessId</code>. */
	ModelEnvironment getModelAndWorkbenchEnvironment(String accessId, Set<String> workbenchPerspectiveNames) throws AccessServiceException;

	/** Returns the {@link ModelEnvironment} for the access identified by {@code accessId} under {@code accessDomain}'s context */
	ModelEnvironment getModelEnvironmentForDomain(String accessId, AccessDomain accessDomain) throws AccessServiceException;

	/** Returns the {@link ModelEnvironmentServices} for the access identified by {@code accessId} */
	ModelEnvironmentServices getModelEnvironmentServices(String accessId) throws AccessServiceException;

	/** Returns the {@link ModelEnvironmentServices} for the access identified by {@code accessId} under {@code accessDomain}'s context */
	ModelEnvironmentServices getModelEnvironmentServicesForDomain(String accessId, AccessDomain accessDomain) throws AccessServiceException;

	/** Returns a set of all configured {@link IncrementalAccess} IDs. */
	Set<String> getAccessIds() throws AccessServiceException;

	/** Returns the {@link GmMetaModel} of the access identified by <code>accessId</code>. */
	GmMetaModel getMetaModel(String accessId) throws AccessServiceException;

	/**
	 * Returns the <code>GmMetaModel</code> for the specified types (which are queried from cortex access) and all types that are reachable from those
	 * types when traversing the skeleton (i.e. via super types or property types, but NOT <code>MetaData</code>). To make the returned
	 * <code>GmMetaModel</code> valid, it always also contains (all) simple types and the base type. For the same reason, the artifact binding is set
	 * (to <code>virtual:Virtual#0.0</code>).
	 */
	GmMetaModel getMetaModelForTypes(Set<String> typeSignatures) throws AccessServiceException;

	/** Queries the access identified by <code>accessId</code> with the given {@link SelectQuery} and returns a {@link SelectQueryResult}. */
	SelectQueryResult query(String accessId, SelectQuery query) throws AccessServiceException;

	/** Queries the access identified by <code>accessId</code> for a property of a {@link GenericEntity}. */
	PropertyQueryResult queryProperty(String accessId, PropertyQuery request) throws AccessServiceException;

	/** Queries the access identified by <code>accessId</code> and returns an {@link EntityQueryResult}. */
	EntityQueryResult queryEntities(String accessId, EntityQuery request) throws AccessServiceException;

	/** Applies the manipulation specified in the {@link ManipulationRequest} to the access identified by <code>accessId</code>. */
	ManipulationResponse applyManipulation(String accessId, ManipulationRequest manipulationRequest) throws AccessServiceException;

	/** Returns a {@link ReferencesResponse} related to the entity specified in the given {@link ReferencesRequest}. */
	ReferencesResponse getReferences(String accessId, ReferencesRequest referencesRequest) throws AccessServiceException;

	Set<String> getPartitions(String accessId) throws AccessServiceException;
}
