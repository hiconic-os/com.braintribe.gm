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

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.processing.core.expert.api.MutableDenotationMap;
import com.braintribe.model.processing.core.expert.impl.PolymorphicDenotationMap;
import com.braintribe.model.processing.service.api.InterceptingServiceProcessorBuilder;
import com.braintribe.model.processing.service.api.ReasonedServiceProcessor;
import com.braintribe.model.processing.service.api.ServiceAroundProcessor;
import com.braintribe.model.processing.service.api.ServiceInterceptorProcessor;
import com.braintribe.model.processing.service.api.ServicePostProcessor;
import com.braintribe.model.processing.service.api.ServicePreProcessor;
import com.braintribe.model.processing.service.api.ServiceProcessor;
import com.braintribe.model.processing.service.api.ServiceRegistry;
import com.braintribe.model.processing.service.api.ServiceRequestContext;
import com.braintribe.model.service.api.ServiceRequest;

public class ConfigurableDispatchingServiceProcessor implements ServiceProcessor<ServiceRequest, Object>, ServiceRegistry {

	private static final ReasonedServiceProcessor<ServiceRequest, Object> DEFAULT_PROCESSOR = (c, r) -> { 
		throw new UnsupportedOperationException("No service processor mapped for request: " + r); 
	}; 
	
	private final MutableDenotationMap<ServiceRequest, ServiceProcessor<? extends ServiceRequest, ?>> processorMap;
	
	private final List<InterceptorEntry> interceptors = new ArrayList<>();

	private static class InterceptorEntry {
		String identification;
		Predicate<ServiceRequest> filter;
		ServiceInterceptorProcessor interceptor;
		
		public InterceptorEntry(String identifier, Predicate<ServiceRequest> filter, ServiceInterceptorProcessor interceptor) {
			super();
			this.identification = identifier;
			this.filter = filter;
			this.interceptor = interceptor;
		}
	}
	
	public ConfigurableDispatchingServiceProcessor() {
		this(new PolymorphicDenotationMap<>());
	}

	public ConfigurableDispatchingServiceProcessor(
			MutableDenotationMap<ServiceRequest, ServiceProcessor<? extends ServiceRequest, ?>> processorMap) {
		this.processorMap = processorMap;
	}
	
	public <R extends ServiceRequest> void register(EntityType<R> requestType, ServiceProcessor<? super R, ?> serviceProcessor) {
		processorMap.put(requestType, serviceProcessor);  
	}
	
	public InterceptorRegistration registerInterceptor(String identification) {
		return new InterceptorRegistration() {
			
			private String insertIdentification;
			private boolean before;
			
			@Override
			public void register(ServiceInterceptorProcessor interceptor) {
				registerWithPredicate(r -> true, interceptor);
			}
			
			@Override
			public <R extends ServiceRequest> void registerForType(EntityType<R> requestType, ServiceInterceptorProcessor interceptor) {
				registerWithPredicate(r -> requestType.isInstance(r), interceptor);
			}
			
			@Override
			public void registerWithPredicate(Predicate<ServiceRequest> predicate, ServiceInterceptorProcessor interceptor) {
				InterceptorEntry interceptorEntry = new InterceptorEntry(identification, predicate, interceptor);
				
				if (insertIdentification != null) {
					requireInterceptorIterator(insertIdentification, before).add(interceptorEntry);
				}
				else {
					interceptors.add(interceptorEntry);
				}
			}
			
			@Override
			public InterceptorRegistration before(String identification) {
				this.insertIdentification = identification;
				this.before = true;
				return this;
			}
			
			@Override
			public InterceptorRegistration after(String identification) {
				this.insertIdentification = identification;
				this.before = false;
				return this;
			}
		};
	}
	
	private ListIterator<InterceptorEntry> find(String identification, boolean before) {
		ListIterator<InterceptorEntry> it = interceptors.listIterator();
		while (it.hasNext()) {
			InterceptorEntry entry = it.next();
			if (entry.identification.equals(identification)) {
				if (before)
					it.previous();
				break;
			}
		}
		
		return it;
	}
	
	private ListIterator<InterceptorEntry> requireInterceptorIterator(String identification, boolean before) {
		ListIterator<InterceptorEntry> iterator = find(identification, before);
		
		if (!iterator.hasNext())
			throw new NoSuchElementException("No processor found with identification: '" + identification + "'");
		
		return iterator;
	}
	
	public void removeInterceptor(String identification) {
		requireInterceptorIterator(identification, true).remove();
	}
	
	private ServiceProcessor<ServiceRequest, Object> getProcessor(ServiceRequest request) {
		ServiceProcessor<ServiceRequest, Object> processor = processorMap.find(request);
		
		if (processor == null)
			return DEFAULT_PROCESSOR;
		
		return processor;
	}
	
	@Override
	public Object process(ServiceRequestContext requestContext, ServiceRequest request) {
		final ServiceProcessor<ServiceRequest, Object> processor;
		
		if (interceptors == null || interceptors.isEmpty()) {
			processor = getProcessor(request);
		}
		else {
			processor = getInterceptingProcessor(request);
		}
			
		return processor.process(requestContext, request);
	}


	private ServiceProcessor<ServiceRequest, Object> getInterceptingProcessor(ServiceRequest request) {
		ServiceProcessor<?, ?> processor = getProcessor(request);
		
		InterceptingServiceProcessorBuilder builder = ServiceProcessingChain.create(processor); //
		
		boolean hasAroundProcessors = false;
		
		for (InterceptorEntry entry: interceptors) {
			if (entry.filter.test(request)) {
				ServiceInterceptorProcessor interceptor = entry.interceptor;
				switch (interceptor.getKind()) {
					case pre: 
						builder.preProcessWith((ServicePreProcessor<?>) interceptor);
						break;
					case around:
						hasAroundProcessors = true;
						builder.aroundProcessWith((ServiceAroundProcessor<?, ?>) interceptor);
						break;
					case post: 
						builder.postProcessWith((ServicePostProcessor<?>) interceptor);
						break;
					default:
						throw new UnsupportedOperationException("Unsupported InterceptorKind: " + interceptor.getKind());
				}
			}
		}
		
		if (!hasAroundProcessors && processor == DEFAULT_PROCESSOR)
			return DEFAULT_PROCESSOR;
		
		return builder.build();
	}
}
