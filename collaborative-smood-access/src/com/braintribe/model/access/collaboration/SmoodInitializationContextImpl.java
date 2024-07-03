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
package com.braintribe.model.access.collaboration;

import java.util.Map;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.manipulation.Manipulation;
import com.braintribe.model.generic.tracking.ManipulationListener;
import com.braintribe.model.processing.session.api.collaboration.PersistenceInitializationContext;
import com.braintribe.model.processing.session.api.managed.ManagedGmSession;
import com.braintribe.model.smoodstorage.stages.PersistenceStage;

/**
 * @author peter.gazdik
 */
public class SmoodInitializationContextImpl implements PersistenceInitializationContext, ManipulationListener {

	private final ManagedGmSession session;
	private final StageRegistry stageRegistry;

	private PersistenceStage currentStage;
	private final String accessId;
	
	private Map<String, Object> attributes;

	public SmoodInitializationContextImpl(ManagedGmSession session, StageRegistry stageRegistry, String accessId) {
		this.session = session;
		this.stageRegistry = stageRegistry;
		this.accessId = accessId;

		session.listeners().add(this);
	}
	
	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	@Override
	public ManagedGmSession getSession() {
		return session;
	}
	
	@Override
	public String getAccessId() {
		return accessId;
	}

	@Override
	public PersistenceStage getStage(GenericEntity entity) {
		return stageRegistry.getStage(entity);
	}

	@Override
	public void setCurrentPersistenceStage(PersistenceStage stage) {
		this.currentStage = stage;
	}

	public void close() {
		session.listeners().remove(this);
	}

	@Override
	public void noticeManipulation(Manipulation m) {
		stageRegistry.onManipulation(m, currentStage);
	}

	@Override
	public <T> T getAttribute(String key) {
		return attributes != null ? (T) attributes.get(key) : null;
	}
	
	
}
