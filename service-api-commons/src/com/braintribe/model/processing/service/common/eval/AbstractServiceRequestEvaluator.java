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
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.braintribe.cfg.Configurable;
import com.braintribe.common.attribute.AttributeContextBuilder;
import com.braintribe.ddsa.chain.DDSA_EvalContext;
import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.ReasonException;
import com.braintribe.model.generic.eval.EvalContext;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.processing.service.api.ServiceProcessor;
import com.braintribe.model.service.api.ServiceRequest;
import com.braintribe.utils.lcd.NullSafe;

/**
 * <p>
 * Standard local abstract {@link Evaluator} of {@link ServiceRequest}(s).
 * 
 * @author Dirk Scheffler
 */
public abstract class AbstractServiceRequestEvaluator implements Evaluator<ServiceRequest> {

	public ServiceProcessor<ServiceRequest, Object> serviceProcessor;
	public ExecutorService executorService;
	public Evaluator<ServiceRequest> contextEvaluator = this;
	public Function<Reason, RuntimeException> reasonExceptionFactory = ReasonException::new;
	public Consumer<AttributeContextBuilder> attributesConfigurer = null;

	@Configurable
	public void setAttributesConfigurer(Consumer<AttributeContextBuilder> attributesConfigurer) {
		this.attributesConfigurer = attributesConfigurer;
	}
	
	public Consumer<AttributeContextBuilder> getAttributesConfigurer() {
		return attributesConfigurer;
	}
	
	@Configurable
	public void setReasonExceptionFactory(Function<Reason, RuntimeException> reasonToExceptionTransformator) {
		this.reasonExceptionFactory = reasonToExceptionTransformator;
	}
	
	public void setContextEvaluator(Evaluator<ServiceRequest> contextEvaluator) {
		this.contextEvaluator = contextEvaluator;
	}

	public void setServiceProcessor(ServiceProcessor<ServiceRequest, Object> serviceProcessor) {
		this.serviceProcessor = serviceProcessor;
	}

	public void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}

	@Override
	public <T> EvalContext<T> eval(ServiceRequest serviceRequest) {
		NullSafe.nonNull(serviceRequest, "serviceRequest");
		return new DDSA_EvalContext<T>(this, serviceRequest);
	}

}
