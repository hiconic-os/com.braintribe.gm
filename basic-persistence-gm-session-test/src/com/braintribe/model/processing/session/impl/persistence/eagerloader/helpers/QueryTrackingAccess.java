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
package com.braintribe.model.processing.session.impl.persistence.eagerloader.helpers;

import java.util.Set;

import com.braintribe.model.access.IncrementalAccess;
import com.braintribe.model.access.ModelAccessException;
import com.braintribe.model.accessapi.ManipulationRequest;
import com.braintribe.model.accessapi.ManipulationResponse;
import com.braintribe.model.accessapi.ReferencesRequest;
import com.braintribe.model.accessapi.ReferencesResponse;
import com.braintribe.model.generic.reflection.GenericModelException;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.processing.query.tools.PreparedTcs;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.processing.session.impl.persistence.EagerLoaderSupportingAccess;
import com.braintribe.model.query.EntityQuery;
import com.braintribe.model.query.EntityQueryResult;
import com.braintribe.model.query.PropertyQuery;
import com.braintribe.model.query.PropertyQueryResult;
import com.braintribe.model.query.SelectQuery;
import com.braintribe.model.query.SelectQueryResult;

/**
 * @author peter.gazdik
 */
public class QueryTrackingAccess implements EagerLoaderSupportingAccess {

	private final IncrementalAccess delegate;
	private final AccessQueryListener queryListener;

	public QueryTrackingAccess(IncrementalAccess delegate, AccessQueryListener queryListener) {
		this.delegate = delegate;
		this.queryListener = queryListener;
	}

	@Override
	public String getAccessId() {
		return delegate.getAccessId();
	}

	@Override
	public GmMetaModel getMetaModel() throws GenericModelException {
		return delegate.getMetaModel();
	}

	@Override
	public SelectQueryResult query(SelectQuery query, PersistenceGmSession session) throws ModelAccessException {
		checkCriteriaIsScalarOnly(query);

		return session.query().select(query).result();
	}

	private void checkCriteriaIsScalarOnly(SelectQuery query) {
		if (query.getTraversingCriterion() != PreparedTcs.scalarOnlyTc)
			throw new RuntimeException("ERROR IN TEST. We assume the TC for this query triggered by the EagerLoader only loads scalar properties"
					+ ", but something changed in the implementation.");
	}

	@Override
	public SelectQueryResult query(SelectQuery query) throws ModelAccessException {
		queryListener.onQuery(query);
		return delegate.query(query);
	}

	@Override
	public EntityQueryResult queryEntities(EntityQuery query) throws ModelAccessException {
		queryListener.onQuery(query);
		return delegate.queryEntities(query);
	}

	@Override
	public PropertyQueryResult queryProperty(PropertyQuery query) throws ModelAccessException {
		queryListener.onQuery(query);
		return delegate.queryProperty(query);
	}

	@Override
	public ManipulationResponse applyManipulation(ManipulationRequest manipulationRequest) throws ModelAccessException {
		return delegate.applyManipulation(manipulationRequest);
	}

	@Override
	public ReferencesResponse getReferences(ReferencesRequest referencesRequest) throws ModelAccessException {
		return delegate.getReferences(referencesRequest);
	}

	@Override
	public Set<String> getPartitions() throws ModelAccessException {
		return delegate.getPartitions();
	}

}
