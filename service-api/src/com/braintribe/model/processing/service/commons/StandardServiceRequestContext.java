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
package com.braintribe.model.processing.service.commons;



import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import com.braintribe.common.attribute.AttributeContext;
import com.braintribe.common.attribute.TypeSafeAttribute;
import com.braintribe.model.generic.eval.EvalContext;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.processing.service.api.ServiceRequestContext;
import com.braintribe.model.processing.service.api.ServiceRequestContextAspect;
import com.braintribe.model.processing.service.api.ServiceRequestContextBuilder;
import com.braintribe.model.processing.service.api.ServiceRequestSummaryLogger;
import com.braintribe.model.processing.service.api.aspect.EagerResponseConsumerAspect;
import com.braintribe.model.processing.service.api.aspect.SummaryLoggerAspect;
import com.braintribe.model.service.api.ServiceRequest;
import com.braintribe.utils.collection.impl.MapAttributeContext;

/**
 * <p>
 * A standard implementation of {@link ServiceRequestContext} which enables context enriching.
 * 
 * @author neidhart.orlich
 * @author dirk.scheffler
 */
public class StandardServiceRequestContext extends MapAttributeContext implements ServiceRequestContext {
	
	private ServiceRequestSummaryLogger summaryLogger;
	private Evaluator<ServiceRequest> evaluator;
	
	public StandardServiceRequestContext(Evaluator<ServiceRequest> evaluator) {
		this.evaluator = evaluator;
	}
	
	public StandardServiceRequestContext(AttributeContext parent, Evaluator<ServiceRequest> evaluator) {
		super(parent);
		Objects.requireNonNull(evaluator, "Argument evaluator must not be null");
		this.evaluator = evaluator;
	}
	
	public StandardServiceRequestContext(ServiceRequestContext parent) {
		super(parent);
		this.evaluator = parent.getEvaluator();
	}
	
	public void setEvaluator(Evaluator<ServiceRequest> evaluator) {
		checkMutability();
		this.evaluator = evaluator;
	}
	
	@Override
	public Evaluator<ServiceRequest> getEvaluator() {
		return evaluator;
	}
	
	@Override
	public ServiceRequestContextBuilder derive() {
		return new StandardServiceRequestContextBuilder(this);
	}

	public void setSummaryLogger(ServiceRequestSummaryLogger summaryLogger) {
		setAttribute(SummaryLoggerAspect.class, summaryLogger);
	}
	
	@Override
	public <T> EvalContext<T> eval(ServiceRequest evaluable) {
		return evaluator.eval(evaluable);
	}

	@Override
	public ServiceRequestSummaryLogger summaryLogger() {
		if (summaryLogger == null) {
			summaryLogger = getAspect(SummaryLoggerAspect.class).orElse(NoOpServiceRequestSummaryLogger.INSTANCE);
		}

		return summaryLogger;
	}

	@Override
	public <T, A extends ServiceRequestContextAspect<? super T>> T findAspect(Class<A> aspect) {
		return findAttribute((Class<TypeSafeAttribute<T>>)(Class<?>) aspect).orElse(null);
	}

	@Override
	public <T, A extends ServiceRequestContextAspect<? super T>> Optional<T> getAspect(Class<A> aspect) {
		return Optional.ofNullable(findAspect(aspect));
	}
	
	@Override
	public <T, A extends ServiceRequestContextAspect<? super T>> T getAspect(Class<A> aspect, T def) {
		Object value = findAspect(aspect);
		
		if (value == null)
			return def;
		else
			return (T) value;
	}
	
	public Consumer<?> responseConsumer() {
		return findAspect(EagerResponseConsumerAspect.class);
	}
}
