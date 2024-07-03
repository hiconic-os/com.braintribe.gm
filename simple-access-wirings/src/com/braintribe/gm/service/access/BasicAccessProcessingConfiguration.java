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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import com.braintribe.cartridge.common.processing.accessrequest.InternalizingAccessRequestProcessor;
import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.Required;
import com.braintribe.gm.service.access.api.AccessProcessingConfiguration;
import com.braintribe.model.access.IncrementalAccess;
import com.braintribe.model.access.smood.basic.SmoodAccess;
import com.braintribe.model.accessapi.AccessRequest;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.processing.accessrequest.api.AccessRequestProcessor;
import com.braintribe.model.processing.service.api.ServiceProcessor;
import com.braintribe.model.processing.service.common.ConfigurableDispatchingServiceProcessor;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSessionFactory;
import com.braintribe.model.service.api.ServiceRequest;
import com.braintribe.testing.tools.gm.GmTestTools;

public class BasicAccessProcessingConfiguration implements AccessProcessingConfiguration, Consumer<ConfigurableDispatchingServiceProcessor> {
	private final Map<EntityType<? extends AccessRequest>, AccessRequestProcessor<?, ?>> registeredProcessors = new LinkedHashMap<>();
	private List<Consumer<AccessProcessingConfiguration>> accessConfigurers;
	private final SimpleAccessService simpleAccessService = new SimpleAccessService();
	private PersistenceGmSessionFactory sessionFactory;

	public BasicAccessProcessingConfiguration() {
	}

	public <R extends AccessRequest, T> ServiceProcessor<R, T> toServiceProcessor(AccessRequestProcessor<R, T> serviceProcessor) {
		InternalizingAccessRequestProcessor<R, AccessRequestProcessor<R, T>> wrappingProcessor = new InternalizingAccessRequestProcessor<>(
				serviceProcessor, sessionFactory, sessionFactory);

		return (ServiceProcessor<R, T>) wrappingProcessor;
	}

	@Configurable
	@Required
	public void setSessionFactory(PersistenceGmSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public void registerAccess(IncrementalAccess access) {
		simpleAccessService.addAccess(access);
	}

	@Override
	public SmoodAccess registerAccess(String accessId, GmMetaModel metaModel) {
		SmoodAccess access = GmTestTools.newSmoodAccessMemoryOnly(accessId, metaModel);
		registerAccess(access);
		return access;
	}

	@Override
	public <A extends AccessRequest> void registerAccessRequestProcessor(EntityType<A> requestType, AccessRequestProcessor<? super A, ?> processor) {
		registeredProcessors.put(requestType, processor);
	}

	public SimpleAccessService getSimpleAccessService() {
		return simpleAccessService;
	}

	@Override
	public void accept(ConfigurableDispatchingServiceProcessor dispatcher) {
		accessConfigurers.forEach(c -> c.accept(this));
		registeredProcessors.forEach((t, p) -> dispatcher.register(t, (ServiceProcessor<ServiceRequest, ?>) toServiceProcessor(p)));
	}

	public void setAccessConfigurers(List<Consumer<AccessProcessingConfiguration>> accessConfigurers) {
		this.accessConfigurers = accessConfigurers;
	}

	@Override
	public void registerAccess(BiFunction<String, GmMetaModel, IncrementalAccess> accessFactory, String accessId, GmMetaModel model) {
		IncrementalAccess access = accessFactory.apply(accessId, model);
		registerAccess(access);
	}
}
