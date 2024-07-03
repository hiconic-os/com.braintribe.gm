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
package com.braintribe.model.processing.garbagecollection;

import java.util.Set;

import com.braintribe.gwt.utils.genericmodel.EntitiesFinder;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;

public class PreassignedEntitiesFinder implements EntitiesFinder {

	Set<GenericEntity> preassignedEntities;

	public PreassignedEntitiesFinder() {
	}

	public PreassignedEntitiesFinder(final Set<GenericEntity> preassignedEntities) {
		this.preassignedEntities = preassignedEntities;
	}

	public Set<GenericEntity> getPreassignedEntities() {
		return this.preassignedEntities;
	}

	public void setPreassignedEntities(final Set<GenericEntity> preassignedEntities) {
		this.preassignedEntities = preassignedEntities;
	}

	// TODO add comment
	@Override
	public Set<GenericEntity> findEntities(final PersistenceGmSession session) {
		return getPreassignedEntities();
	}

}
