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
package com.braintribe.gm.service.wire.common.space;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.braintribe.gm.service.wire.common.contract.ServiceProcessingConfigurationContract;
import com.braintribe.model.processing.securityservice.commons.service.InMemorySecurityServiceProcessor;
import com.braintribe.model.processing.service.common.ConfigurableDispatchingServiceProcessor;
import com.braintribe.wire.api.annotation.Managed;

@Managed
public class ServiceProcessingConfigurationSpace implements ServiceProcessingConfigurationContract {

	@Override
	public void registerSecurityConfigurer(Consumer<InMemorySecurityServiceProcessor> configurer) {
		securityConfigurers().add(configurer);
	}
	
	@Override
	public void registerServiceConfigurer(Consumer<ConfigurableDispatchingServiceProcessor> configurer) {
		serviceConfigurers().add(configurer);
	}
	
	@Managed
	public List<Consumer<ConfigurableDispatchingServiceProcessor>> serviceConfigurers() {
		return new ArrayList<>();
	}
	
	@Managed
	public List<Consumer<InMemorySecurityServiceProcessor>> securityConfigurers() {
		return new ArrayList<>();
	}

	
}
