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
package com.braintribe.model.processing.mpc.evaluator.impl.builder;

import com.braintribe.model.generic.path.api.IModelPathElement;
import com.braintribe.model.processing.mpc.evaluator.api.MpcEvaluatorContext;
import com.braintribe.model.processing.mpc.evaluator.api.MpcEvaluatorRuntimeException;
import com.braintribe.model.processing.mpc.evaluator.api.MpcMatch;
import com.braintribe.model.processing.mpc.evaluator.api.MpcRegistry;
import com.braintribe.model.processing.mpc.evaluator.api.builder.MpcContextBuilder;
import com.braintribe.model.processing.mpc.evaluator.impl.MpcEvaluatorContextImpl;

public class MpcContextBuilderImpl implements MpcContextBuilder {

	protected MpcEvaluatorContext context = new MpcEvaluatorContextImpl();

	@Override
	public MpcContextBuilder withRegistry(MpcRegistry registry) {
		this.context.setMpcRegistry(registry);
		return this;
	}

	@Override
	public MpcMatch mpcMatches(Object condition, IModelPathElement element) throws MpcEvaluatorRuntimeException {
		return this.context.matches(condition, element);
	}

}
