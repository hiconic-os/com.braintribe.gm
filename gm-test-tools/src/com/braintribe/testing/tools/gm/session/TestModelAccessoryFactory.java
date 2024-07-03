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
package com.braintribe.testing.tools.gm.session;

import java.util.HashMap;
import java.util.Map;

import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.processing.session.api.managed.ModelAccessory;
import com.braintribe.model.processing.session.api.managed.ModelAccessoryFactory;

public class TestModelAccessoryFactory implements ModelAccessoryFactory {

	Map<String, ModelAccessory> accessModelAccessories = new HashMap<>();
	Map<String, ModelAccessory> serviceModelAccessories = new HashMap<>();

	@Override
	public ModelAccessory getForAccess(String accessId) {
		return accessModelAccessories.computeIfAbsent(accessId, k -> {
			throw new IllegalArgumentException("Unknown access id: " + accessId);
		});
	}

	@Override
	public ModelAccessory getForServiceDomain(String serviceDomainId) {
		return serviceModelAccessories.computeIfAbsent(serviceDomainId, k -> {
			throw new IllegalArgumentException("Unknown domain id: " + serviceDomainId);
		});
	}

	public void registerAccessModelAccessory(String domainId, GmMetaModel model) {
		registerModelAccessory(domainId, model, accessModelAccessories);
	}

	public void registerServiceModelAccessory(String domainId, GmMetaModel model) {
		registerModelAccessory(domainId, model, serviceModelAccessories);
	}

	private void registerModelAccessory(String domainId, GmMetaModel model, Map<String, ModelAccessory> map) {
		TestModelAccessory modelAccessory = TestModelAccessory.newModelAccessory(model);
		map.put(domainId, modelAccessory);
	}

}
