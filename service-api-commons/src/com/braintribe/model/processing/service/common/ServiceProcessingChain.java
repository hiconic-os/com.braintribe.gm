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

import static com.braintribe.utils.lcd.CollectionTools2.isEmpty;

import java.util.ArrayList;
import java.util.List;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.UnsatisfiedMaybeTunneling;
import com.braintribe.model.processing.service.api.InterceptingServiceProcessorBuilder;
import com.braintribe.model.processing.service.api.ProceedContext;
import com.braintribe.model.processing.service.api.ProceedContextBuilder;
import com.braintribe.model.processing.service.api.ServiceAroundProcessor;
import com.braintribe.model.processing.service.api.ServiceInterceptionChainBuilder;
import com.braintribe.model.processing.service.api.ServiceInterceptorProcessor;
import com.braintribe.model.processing.service.api.ServicePostProcessor;
import com.braintribe.model.processing.service.api.ServicePreProcessor;
import com.braintribe.model.processing.service.api.ServiceProcessor;
import com.braintribe.model.processing.service.api.ServiceRequestContext;
import com.braintribe.model.service.api.ServiceRequest;
import com.braintribe.model.service.api.result.OverridingPostProcessResponse;
import com.braintribe.utils.collection.impl.AttributeContexts;

/**
 * TODO: proceeding must check the type invariance for the ServiceRequest in order to keep the selected ServiceProcessor
 * a valid match
 * 
 * @author Dirk Scheffler
 *
 */
public class ServiceProcessingChain implements ServiceProcessor<ServiceRequest, Object>, ProceedContext {
	private static final ServiceProcessor<ServiceRequest, Object> DEFAULT_PROCESSOR = (c, r) -> {
		throw new UnsupportedOperationException("No service processor mapped for request: " + r);
	};

	private List<ServicePreProcessor<ServiceRequest>> preProcessors;
	private List<ServicePostProcessor<Object>> postProcessors;
	private List<ServiceAroundProcessor<ServiceRequest, ?>> aroundProcessors;
	private ServiceRequestContext serviceRequestContext;

	private ServiceProcessor<ServiceRequest, ?> processor = DEFAULT_PROCESSOR;

	private static <T extends ServiceInterceptorProcessor, E extends T> List<E> appendInterceptor(List<E> list, T element) {
		if (list == null)
			list = new ArrayList<>();

		list.add((E) element);
		return list;
	}

	public static InterceptingServiceProcessorBuilder create() {
		return create(DEFAULT_PROCESSOR);
	}

	public static InterceptingServiceProcessorBuilder create(ServiceProcessor<?, ?> processor) {
		return new InterceptingServiceProcessorBuilderImpl(processor);
	}

	private static abstract class AbstractServiceInterceptionBuilder implements ServiceInterceptionChainBuilder {
		final protected ServiceProcessingChain chain;

		private AbstractServiceInterceptionBuilder() {
			chain = new ServiceProcessingChain();
		}

		@Override
		public void preProcessWith(ServicePreProcessor<?> preProcessor) {
			chain.preProcessors = appendInterceptor(chain.preProcessors, preProcessor);
		}

		@Override
		public void postProcessWith(ServicePostProcessor<?> postProcessor) {
			chain.postProcessors = appendInterceptor(chain.postProcessors, postProcessor);
		}

		@Override
		public void aroundProcessWith(ServiceAroundProcessor<?, ?> aroundProcessor) {
			chain.aroundProcessors = appendInterceptor(chain.aroundProcessors, aroundProcessor);
		}
	}

	private static class ProceedContextBuilderImpl extends AbstractServiceInterceptionBuilder implements ProceedContextBuilder {

		public ProceedContextBuilderImpl(ServiceRequestContext requestContext, ServiceProcessor<?, ?> processor) {
			chain.processor = (ServiceProcessor<ServiceRequest, ?>) processor;
			chain.serviceRequestContext = requestContext;
		}

		@Override
		public ProceedContext build() {
			return chain;
		}
	}

	private static class InterceptingServiceProcessorBuilderImpl extends AbstractServiceInterceptionBuilder
			implements InterceptingServiceProcessorBuilder {

		public InterceptingServiceProcessorBuilderImpl(ServiceProcessor<?, ?> processor) {
			chain.processor = (ServiceProcessor<ServiceRequest, ?>) processor;
		}

		@Override
		public ServiceProcessor<ServiceRequest, Object> build() {
			return chain;
		}
	}

	@Override
	public Object process(ServiceRequestContext requestContext, ServiceRequest request) {
		request = preProcess(requestContext, request);
		Object response = aroundProcess(requestContext, request);
		response = postProcess(requestContext, response);
		return response;
	}

	@Override
	public <T> T proceed(ServiceRequest serviceRequest) {
		return (T) process(serviceRequestContext, serviceRequest);
	}

	@Override
	public <T> T proceed(ServiceRequestContext context, ServiceRequest request) {
		if (context != serviceRequestContext)
			AttributeContexts.push(context);

		try {
			return (T) process(context, request);
		} finally {
			if (context != serviceRequestContext)
				AttributeContexts.pop();
		}
	}

	@Override
	public <T> Maybe<T> proceedReasoned(ServiceRequest request) {
		try {
			return proceed(request);
		} catch (UnsatisfiedMaybeTunneling m) {
			return m.getMaybe();
		}
	}

	@Override
	public <T> Maybe<T> proceedReasoned(ServiceRequestContext context, ServiceRequest request) {
		try {
			return proceed(context, request);
		} catch (UnsatisfiedMaybeTunneling m) {
			return m.getMaybe();
		}
	}

	@Override
	public ProceedContextBuilder newInterceptionChain(ServiceProcessor<?, ?> processor) {
		return new ProceedContextBuilderImpl(serviceRequestContext, processor);
	}

	@Override
	public ProceedContextBuilder extend() {
		return new ProceedContextBuilderImpl(serviceRequestContext, this);
	}

	private ServiceRequest preProcess(ServiceRequestContext requestContext, ServiceRequest request) {
		if (!isEmpty(preProcessors))
			for (ServicePreProcessor<ServiceRequest> preProcessor : preProcessors) 
				request = preProcessor.process(requestContext, request);

		return request;
	}

	private Object aroundProcess(ServiceRequestContext requestContext, ServiceRequest request) {
		if (isEmpty(aroundProcessors))
			return processor.process(requestContext, request);

		ImmutableProceedContext proceedContext = new ImmutableProceedContext(requestContext, 0);
		return proceedContext.proceed(request);
	}

	@SuppressWarnings("deprecation")
	private Object postProcess(ServiceRequestContext requestContext, Object response) {
		if (isEmpty(postProcessors))
			return response;

		for (ServicePostProcessor<Object> postProcessor : postProcessors) {
			Object postProcessorResponse = postProcessor.process(requestContext, response);

			if (response != postProcessorResponse) {
				if (postProcessorResponse instanceof OverridingPostProcessResponse) {
					response = ((OverridingPostProcessResponse) postProcessorResponse).getResponse();
				}
				response = postProcessorResponse;
			}
		}

		return response;
	}

	protected ProceedContext newProceedContext(ServiceRequestContext requestContext) {
		return new ImmutableProceedContext(requestContext, 0);
	}

	private class ImmutableProceedContext implements ProceedContext, ServiceProcessor<ServiceRequest, Object> {
		private final int index;
		private final ServiceRequestContext requestContext;

		public ImmutableProceedContext(ServiceRequestContext requestContext, int index) {
			super();
			this.requestContext = requestContext;
			this.index = index;
		}

		@Override
		public Object process(ServiceRequestContext requestContext, ServiceRequest request) {
			// no around processors left -> call the actual processor 
			if (index >= aroundProcessors.size())
				return processor.process(requestContext, request);

			// call the next around processor with the next proceed context   
			ServiceAroundProcessor<ServiceRequest, ?> nextAroundProcessor = aroundProcessors.get(index);
			ImmutableProceedContext nextProceedCtx = new ImmutableProceedContext(requestContext, index + 1);

			return nextAroundProcessor.process(requestContext, request, nextProceedCtx);
		}

		@Override
		public <T> T proceed(ServiceRequest serviceRequest) {
			// no around processors left -> call the actual processor 
			if (index >= aroundProcessors.size())
				return (T) processor.process(requestContext, serviceRequest);

			// call the next around processor with the next proceed context   
			ServiceAroundProcessor<ServiceRequest, ?> nextAroundProcessor = aroundProcessors.get(index);
			ImmutableProceedContext nextProceedCtx = new ImmutableProceedContext(requestContext, index + 1);

			return (T) nextAroundProcessor.process(requestContext, serviceRequest, nextProceedCtx);
		}

		@Override
		public <T> T proceed(ServiceRequestContext context, ServiceRequest serviceRequest) {
			if (context != requestContext)
				AttributeContexts.push(context);

			try {
				// no around processors left -> call the actual processor
				if (index >= aroundProcessors.size())
					return (T) processor.process(context, serviceRequest);

				// call the next around processor with the next proceed context
				ServiceAroundProcessor<ServiceRequest, ?> nextAroundProcessor = aroundProcessors.get(index);
				ImmutableProceedContext nextProceedCtx = new ImmutableProceedContext(context, index + 1);

				return (T) nextAroundProcessor.process(context, serviceRequest, nextProceedCtx);

			} finally {
				if (context != requestContext)
					AttributeContexts.pop();
			}
		}

		@Override
		public <T> Maybe<T> proceedReasoned(ServiceRequest request) {
			try {
				return Maybe.complete(proceed(request));
			} catch (UnsatisfiedMaybeTunneling m) {
				return m.getMaybe();
			}
		}

		@Override
		public <T> Maybe<T> proceedReasoned(ServiceRequestContext context, ServiceRequest request) {
			try {
				return Maybe.complete(proceed(context, request));
			} catch (UnsatisfiedMaybeTunneling m) {
				return m.getMaybe();
			}
		}

		@Override
		public ProceedContextBuilder extend() {
			return new ProceedContextBuilderImpl(requestContext, this);
		}

		@Override
		public ProceedContextBuilder newInterceptionChain(ServiceProcessor<?, ?> processor) {
			return new ProceedContextBuilderImpl(requestContext, processor);
		}

	}
}