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

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.manipulation.EntityProperty;
import com.braintribe.model.generic.session.exception.GmSessionException;
import com.braintribe.model.generic.value.EntityReference;
import com.braintribe.model.processing.session.api.managed.ManagedGmSession;

public class BasicIdentityCompetence extends AbstractIdentityCompetence {
	protected ManagedGmSession managedSession;
	
	public BasicIdentityCompetence(ManagedGmSession managedSession) {
		super(managedSession);
		this.managedSession = managedSession;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends GenericEntity> T findExistingEntity(EntityReference entityReference) throws IdentityCompetenceException {
		try {
			return (T)managedSession.query().entity(entityReference).find();
		} catch (GmSessionException e) {
			throw new IdentityCompetenceException("error while finding potentially existing entity", e);
		}
	}
	
	@Override
	public boolean wasPropertyManipulated(EntityProperty entityProperty) throws IdentityCompetenceException {
		return false;
	}

	@Override
	public boolean isPreliminarilyDeleted(EntityReference entityReference) throws IdentityCompetenceException {
		return false;
	}
	
}
