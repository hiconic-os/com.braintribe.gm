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
package com.braintribe.model.processing.vde.evaluator.impl.builder;

import com.braintribe.model.processing.vde.evaluator.api.VdeContext;
import com.braintribe.model.processing.vde.evaluator.api.VdeContextAspect;
import com.braintribe.model.processing.vde.evaluator.api.VdeEvaluationMode;
import com.braintribe.model.processing.vde.evaluator.api.VdeRegistry;
import com.braintribe.model.processing.vde.evaluator.api.VdeRuntimeException;
import com.braintribe.model.processing.vde.evaluator.api.builder.VdeContextBuilder;
import com.braintribe.model.processing.vde.evaluator.impl.VdeContextImpl;

/**
 * Implementation of VdeContextBuilder
 * 
 * @see VdeContextBuilder
 * 
 */
public class VdeContextBuilderImpl implements VdeContextBuilder {

	protected VdeContext context = new VdeContextImpl();

	@Override
	public <T, A extends VdeContextAspect<? super T>> VdeContextBuilder with(Class<A> aspect, T value) {
		this.context.put(aspect, value);
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T forValue(Object value) throws VdeRuntimeException {
		return (T) this.context.evaluate(value);
	}

	@Override
	public VdeContextBuilder withEvaluationMode(VdeEvaluationMode mode) {
		this.context.setEvaluationMode(mode);
		return this;
	}

	@Override
	public VdeContextBuilder withRegistry(VdeRegistry registry) {
		this.context.setVdeRegistry(registry);
		return this;
	}

}
