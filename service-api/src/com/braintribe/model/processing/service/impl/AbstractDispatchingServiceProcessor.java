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
package com.braintribe.model.processing.service.impl;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.processing.core.expert.impl.PolymorphicDenotationMap;
import com.braintribe.model.processing.service.api.ServiceProcessor;
import com.braintribe.model.processing.service.api.ServiceRequestContext;
import com.braintribe.model.service.api.ServiceRequest;
import com.braintribe.utils.lcd.LazyInitialized;

public abstract class AbstractDispatchingServiceProcessor<P extends ServiceRequest, R> implements ServiceProcessor<P, R> {

	private final LazyInitialized<DispatchMap<P, R>> lazyDispatchMap = new LazyInitialized<DispatchMap<P, R>>(this::createDispatchMap);

	private DispatchMap<P, R> createDispatchMap() {
		DispatchMap<P, R> dispatchMap = new DispatchMap<>();
		configureDispatching(dispatchMap);
		return dispatchMap;
	}

	protected abstract void configureDispatching(DispatchConfiguration<P, R> dispatching);

	@Override
	public R process(ServiceRequestContext context, P request) {
		ServiceProcessor<P, R> processor = (ServiceProcessor<P, R>) lazyDispatchMap.get().get(request);
		return processor.process(context, request);
	}

	private static class DispatchMap<P1 extends ServiceRequest, R1> //
			extends PolymorphicDenotationMap<P1, ServiceProcessor<? extends P1, ?>> implements DispatchConfiguration<P1, R1> {

		@Override
		public <T extends P1> void register(EntityType<T> denotationType, ServiceProcessor<T, ? extends R1> processor) {
			put(denotationType, processor);
		}

	}
}
