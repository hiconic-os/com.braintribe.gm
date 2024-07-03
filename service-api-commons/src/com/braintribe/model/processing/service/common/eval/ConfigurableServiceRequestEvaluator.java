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
package com.braintribe.model.processing.service.common.eval;

import java.util.concurrent.ExecutorService;

import com.braintribe.cfg.Required;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.processing.service.api.ServiceProcessor;
import com.braintribe.model.service.api.ServiceRequest;

/**
 * Standard local {@link Evaluator} of {@link ServiceRequest}(s).
 * 
 */
public class ConfigurableServiceRequestEvaluator extends AbstractServiceRequestEvaluator {

	@Override
	@Required
	public void setServiceProcessor(ServiceProcessor<ServiceRequest, Object> serviceProcessor) {
		super.setServiceProcessor(serviceProcessor);
	}

	@Override
	@Required
	public void setExecutorService(ExecutorService executorService) {
		super.setExecutorService(executorService);
	}

}