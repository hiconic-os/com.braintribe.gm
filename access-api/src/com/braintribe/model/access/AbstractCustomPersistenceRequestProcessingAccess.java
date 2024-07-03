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

import com.braintribe.model.accessapi.CustomPersistenceRequest;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.processing.core.expert.api.MutableDenotationMap;
import com.braintribe.model.processing.core.expert.impl.PolymorphicDenotationMap;
import com.braintribe.model.processing.service.api.ServiceProcessor;
import com.braintribe.model.processing.service.api.ServiceRequestContext;

/**
 * We will get rid of this once we get rid of {@link AbstractAccess} and only use {@link AccessBase}.
 * 
 * @author peter.gazdik
 */
public abstract class AbstractCustomPersistenceRequestProcessingAccess implements IncrementalAccess {

	private final MutableDenotationMap<CustomPersistenceRequest, ServiceProcessor<?, ?>> customProcessors = new PolymorphicDenotationMap<>(true);

	public <T extends CustomPersistenceRequest> void registerCustomPersistenceRequestProcessor(EntityType<T> entityType,
			ServiceProcessor<? super T, ?> processor) {

		customProcessors.put(entityType, processor);
	}

	@Override
	public Object processCustomRequest(ServiceRequestContext context, CustomPersistenceRequest request) {
		ServiceProcessor<CustomPersistenceRequest, ?> processor = customProcessors.find(request);
		if (processor == null)
			throw new UnsupportedOperationException("Method 'processCustomRequest' is not supported for implementation type: "
					+ getClass().getSimpleName() + ". AccessId: " + getAccessId());

		return processor.process(context, request);
	}

}
