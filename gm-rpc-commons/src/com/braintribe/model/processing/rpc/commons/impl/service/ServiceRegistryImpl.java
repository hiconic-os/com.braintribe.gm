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
package com.braintribe.model.processing.rpc.commons.impl.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.braintribe.cfg.Configurable;
import com.braintribe.logging.Logger;
import com.braintribe.model.processing.rpc.commons.api.GmRpcException;
import com.braintribe.model.processing.rpc.commons.api.service.ConfigurableServiceRegistry;
import com.braintribe.model.processing.rpc.commons.api.service.ServiceDescriptor;

public class ServiceRegistryImpl implements ConfigurableServiceRegistry {

	private static final Logger logger = Logger.getLogger(ServiceRegistryImpl.class);

	private Map<String, ServiceDescriptor<?>> services = new ConcurrentHashMap<String, ServiceDescriptor<?>>();

	@Configurable
	public void setServiceDescriptors(List<ServiceDescriptor<?>> serviceDescriptors) {
		for (ServiceDescriptor<?> desc: serviceDescriptors) {
			try {
				registerServiceDescriptor(desc);
			} catch (GmRpcException e) {
				logger.error("Failed to register service descriptor: "+desc+(e.getMessage() != null ? ": "+e.getMessage() : ""), e);
			}
		}
	}

	@Override
	public <T> ServiceDescriptor<T> getServiceDescriptor(String serviceId) throws GmRpcException {

		if (serviceId == null) {
			throw new GmRpcException("Service id is null, cannot retrieve a service delegate");
		}

		@SuppressWarnings("unchecked")
		ServiceDescriptor<T> descriptor = (ServiceDescriptor<T>) services.get(serviceId);

		if (descriptor == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("No service descriptor found under id '" + serviceId + "'. Registered service ids are: "+getServiceIds());
			}
			throw new GmRpcException("no service with id '" + serviceId + "' registered");
		}

		return descriptor;
	}

	@Override
	public <T> void registerServiceDescriptor(ServiceDescriptor<T> serviceDescriptor) throws GmRpcException {

		if (serviceDescriptor == null) {
			throw new GmRpcException("A null service descriptor cannot be registered");
		}

		ServiceDescriptor<?> previousServiceDescriptor = services.put(serviceDescriptor.getServiceId(), serviceDescriptor);

		if (logger.isDebugEnabled()) {
			logger.debug("Registered service under id '" + serviceDescriptor.getServiceId() + 
							"': "+serviceDescriptor.getService()+(previousServiceDescriptor != null ? 
							" replacing previous service: "+previousServiceDescriptor.getService() :""));
		}

	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> ServiceDescriptor<T> unregisterServiceDescriptor(String serviceId) throws GmRpcException {
		return (ServiceDescriptor<T>)services.remove(serviceId);
	}

	@Override
	public Set<String> getServiceIds() {
		Set<String> keySet = this.services.keySet();
		return keySet;
	}

}
