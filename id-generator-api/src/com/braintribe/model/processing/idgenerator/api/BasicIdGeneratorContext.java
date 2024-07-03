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
package com.braintribe.model.processing.idgenerator.api;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;

public class BasicIdGeneratorContext implements IdGeneratorContext{
	
	private PersistenceGmSession session;
	private PersistenceGmSession systemSession;
	private EntityType<?> entityType;

	public BasicIdGeneratorContext() {
	}

	public BasicIdGeneratorContext(PersistenceGmSession session, PersistenceGmSession systemSession, EntityType<?> entityType) {
		this.session = session;
		this.systemSession = systemSession;
		this.entityType = entityType;
	}

	
	public void setEntityType(EntityType<?> entityType) {
		this.entityType = entityType;
	}
	
	public void setSession(PersistenceGmSession session) {
		this.session = session;
	}
	
	public void setSystemSession(PersistenceGmSession systemSession) {
		this.systemSession = systemSession;
	}
	
	
	@Override
	public EntityType<?> getEntityType() {
		return entityType;
	}
	
	@Override
	public PersistenceGmSession getSession() {
		return session;
	}
	
	@Override
	public PersistenceGmSession getSystemSession() {
		return systemSession;
	}

}
