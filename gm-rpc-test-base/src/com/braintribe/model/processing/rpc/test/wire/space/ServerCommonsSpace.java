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
package com.braintribe.model.processing.rpc.test.wire.space;

import static com.braintribe.wire.api.util.Lists.list;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.braintribe.model.processing.rpc.commons.api.service.ConfigurableServiceRegistry;
import com.braintribe.model.processing.rpc.commons.impl.service.ServiceDescriptorImpl;
import com.braintribe.model.processing.rpc.commons.impl.service.ServiceRegistryImpl;
import com.braintribe.model.processing.rpc.test.service.iface.basic.BasicTestService;
import com.braintribe.model.processing.rpc.test.service.iface.basic.BasicTestServiceImpl;
import com.braintribe.model.processing.rpc.test.service.iface.streaming.StreamingTestService;
import com.braintribe.model.processing.rpc.test.service.iface.streaming.StreamingTestServiceImpl;
import com.braintribe.model.processing.rpc.test.service.processor.basic.BasicTestServiceProcessor;
import com.braintribe.model.processing.rpc.test.service.processor.basic.BasicTestServiceProcessorRequest;
import com.braintribe.model.processing.rpc.test.service.processor.failure.FailureTestServiceProcessor;
import com.braintribe.model.processing.rpc.test.service.processor.failure.FailureTestServiceProcessorRequest;
import com.braintribe.model.processing.rpc.test.service.processor.streaming.DownloadCaptureTestServiceProcessor;
import com.braintribe.model.processing.rpc.test.service.processor.streaming.DownloadCaptureTestServiceProcessorRequest;
import com.braintribe.model.processing.rpc.test.service.processor.streaming.DownloadResourceTestServiceProcessor;
import com.braintribe.model.processing.rpc.test.service.processor.streaming.DownloadResourceTestServiceProcessorRequest;
import com.braintribe.model.processing.rpc.test.service.processor.streaming.StreamingTestServiceProcessor;
import com.braintribe.model.processing.rpc.test.service.processor.streaming.StreamingTestServiceProcessorRequest;
import com.braintribe.model.processing.rpc.test.service.processor.streaming.UploadTestServiceProcessor;
import com.braintribe.model.processing.rpc.test.service.processor.streaming.UploadTestServiceProcessorRequest;
import com.braintribe.model.processing.securityservice.commons.service.AuthorizingServiceInterceptor;
import com.braintribe.model.processing.service.api.ServiceProcessor;
import com.braintribe.model.processing.service.api.ServiceRequestContext;
import com.braintribe.model.processing.service.common.CompositeServiceProcessor;
import com.braintribe.model.processing.service.common.ConfigurableDispatchingServiceProcessor;
import com.braintribe.model.processing.service.common.eval.AuthorizingServiceRequestEvaluator;
import com.braintribe.model.processing.service.common.eval.ConfigurableServiceRequestEvaluator;
import com.braintribe.model.securityservice.SecurityRequest;
import com.braintribe.model.service.api.AuthorizableRequest;
import com.braintribe.model.service.api.CompositeRequest;
import com.braintribe.model.service.api.ServiceRequest;
import com.braintribe.model.usersession.UserSession;
import com.braintribe.provider.Hub;
import com.braintribe.provider.ThreadLocalStackedHolder;
import com.braintribe.thread.impl.ThreadLocalStack;
import com.braintribe.wire.api.annotation.Import;
import com.braintribe.wire.api.annotation.Managed;
import com.braintribe.wire.api.space.WireSpace;

@Managed
public class ServerCommonsSpace implements WireSpace {

	@Import
	private CommonsSpace commons;

	@Import
	private CryptoSpace crypto;

	@Import
	private ClientCommonsSpace clientCommons;

	@Managed
	public AuthorizingServiceInterceptor authorizingInterceptor() {
		AuthorizingServiceInterceptor bean = new AuthorizingServiceInterceptor();
		return bean;
	}

	@Managed
	private ServiceProcessor<ServiceRequest, Object> selectingServiceProcessor() {
		ConfigurableDispatchingServiceProcessor bean = new ConfigurableDispatchingServiceProcessor();
		bean.register(CompositeRequest.T, compositeServiceProcessor());
		bean.register(SecurityRequest.T, commons.securityProcessor());
		bean.register(BasicTestServiceProcessorRequest.T, new BasicTestServiceProcessor());
		bean.register(FailureTestServiceProcessorRequest.T, new FailureTestServiceProcessor());
		bean.register(StreamingTestServiceProcessorRequest.T, new StreamingTestServiceProcessor());
		bean.register(UploadTestServiceProcessorRequest.T, new UploadTestServiceProcessor());
		bean.register(DownloadResourceTestServiceProcessorRequest.T, new DownloadResourceTestServiceProcessor());
		bean.register(DownloadCaptureTestServiceProcessorRequest.T, new DownloadCaptureTestServiceProcessor());
		
		bean.registerInterceptor("auth").registerForType(AuthorizableRequest.T, authorizingInterceptor());
		
		return bean;
	}

	@Managed
	private ConfigurableServiceRegistry serviceRegistry() {
		ServiceDescriptorImpl<BasicTestService> basic = new ServiceDescriptorImpl<>();
		basic.setService(new BasicTestServiceImpl());
		basic.setServiceId(BasicTestService.ID);
		basic.setServiceInterfaceClass(BasicTestService.class);

		ServiceDescriptorImpl<StreamingTestService> streaming = new ServiceDescriptorImpl<>();
		streaming.setService(new StreamingTestServiceImpl());
		streaming.setServiceId(StreamingTestService.ID);
		streaming.setServiceInterfaceClass(StreamingTestService.class);

		ServiceRegistryImpl bean = new ServiceRegistryImpl();
		bean.setServiceDescriptors(list(basic, streaming));

		return bean;
	}

	@Managed
	private Hub<UserSession> userSessionHolder() {
		return new ThreadLocalStackedHolder<>();
	}

	@Managed
	private CompositeServiceProcessor compositeServiceProcessor() {
		return new CompositeServiceProcessor();
	}

	@Managed
	public ThreadLocalStack<ServiceRequestContext> serviceContextStack() {
		return new ThreadLocalStack<>();
	}

	@Managed
	public ConfigurableServiceRequestEvaluator serviceRequestEvaluator() {
		ConfigurableServiceRequestEvaluator bean = new ConfigurableServiceRequestEvaluator();
		bean.setServiceProcessor(selectingServiceProcessor());
		bean.setExecutorService(serviceRequestEvaluatorExecutor());
		return bean;
	}

	@Managed
	private ExecutorService serviceRequestEvaluatorExecutor() {
		return Executors.newCachedThreadPool();
	}

	@Managed
	public AuthorizingServiceRequestEvaluator serviceRequestEvaluatorSelfAuthenticating() {
		AuthorizingServiceRequestEvaluator bean = new AuthorizingServiceRequestEvaluator();
		bean.setDelegate(serviceRequestEvaluator());
		bean.setUserSessionProvider(clientCommons.userSessionProvider());
		return bean;
	}
}
