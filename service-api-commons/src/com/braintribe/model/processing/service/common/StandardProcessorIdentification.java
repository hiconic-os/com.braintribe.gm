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
package com.braintribe.model.processing.service.common;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.processing.service.api.ProcessorIdentification;

public class StandardProcessorIdentification implements ProcessorIdentification {

	private final String serviceId;
	private final EntityType<?> componentType;
	private final String domainId;

	public StandardProcessorIdentification(String serviceId) {
		this(serviceId, null, null);
	}

	public StandardProcessorIdentification(String serviceId, EntityType<?> componentType) {
		this(serviceId, componentType, null);
	}
	
	public StandardProcessorIdentification(String serviceId, EntityType<?> componentType, String domainId) {
		super();
		this.serviceId = serviceId;
		this.componentType = componentType;
		this.domainId = domainId;
	}

	@Override
	public String serviceId() {
		return serviceId;
	}

	@Override
	public <T extends EntityType<?>> T componentType() {
		return (T) componentType;
	}

	@Override
	public String domainId() {
		return domainId;
	}
}
