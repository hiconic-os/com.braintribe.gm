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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.braintribe.cfg.Required;
import com.braintribe.model.access.AccessService;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.processing.session.api.managed.ModelAccessory;
import com.braintribe.model.processing.session.api.managed.ModelAccessoryFactory;
import com.braintribe.model.processing.session.impl.managed.StaticAccessModelAccessory;

public class SimpleAccessServiceModeAccessoryFactory implements ModelAccessoryFactory {
	private Map<String, ModelAccessory> factories = new ConcurrentHashMap<>();
	
	private AccessService accessService;
	
	@Required
	public void setAccessService(AccessService accessService) {
		this.accessService = accessService;
	}
	
	@Override
	public ModelAccessory getForAccess(String accessId) {
		return factories.computeIfAbsent(accessId, this::buildModelAccessory);
	}
	
	private ModelAccessory buildModelAccessory(String accessId) {
		GmMetaModel metaModel = accessService.getMetaModel(accessId);
		StaticAccessModelAccessory modelAccessory = new StaticAccessModelAccessory(metaModel, accessId);
		return modelAccessory;
	}
	
	
}
