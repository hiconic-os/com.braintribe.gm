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
package com.braintribe.gm.service.access.wire.common.space;

import com.braintribe.gm.service.access.BasicAccessProcessingConfiguration;
import com.braintribe.gm.service.access.wire.common.contract.CommonAccessProcessingContract;
import com.braintribe.gm.service.wire.common.space.CommonServiceProcessingSpace;
import com.braintribe.gm.service.wire.common.space.ServiceProcessingConfigurationSpace;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSessionFactory;
import com.braintribe.wire.api.annotation.Import;
import com.braintribe.wire.api.annotation.Managed;
import com.braintribe.wire.api.context.WireContextConfiguration;

@Managed
public class CommonAccessProcessingSpace implements CommonAccessProcessingContract {
	@Import
	private ServiceProcessingConfigurationSpace serviceProcessingConfiguration;
	@Import
	private AccessProcessingConfigurationSpace accessProcessingConfiguration;
	@Import
	private CommonServiceProcessingSpace commonServiceProcessing;
	@Import
	private SessionsSpace sessions;

	@Managed
	public BasicAccessProcessingConfiguration accessRegistry() {
		BasicAccessProcessingConfiguration bean = new BasicAccessProcessingConfiguration();
		bean.setAccessConfigurers(accessProcessingConfiguration.accessConfigurers());
		bean.setSessionFactory(sessionFactory());

		return bean;
	}

	@Override
	public void onLoaded(WireContextConfiguration configuration) {
		serviceProcessingConfiguration.registerServiceConfigurer(c -> accessRegistry().accept(c));
	}

	@Override
	public PersistenceGmSessionFactory sessionFactory() {
		return sessions.sessionFactory();
	}
}
