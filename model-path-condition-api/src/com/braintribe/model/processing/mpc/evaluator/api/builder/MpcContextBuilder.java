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
package com.braintribe.model.processing.mpc.evaluator.api.builder;

import com.braintribe.model.generic.path.api.IModelPathElement;
import com.braintribe.model.processing.mpc.evaluator.api.MpcEvaluatorRuntimeException;
import com.braintribe.model.processing.mpc.evaluator.api.MpcMatch;
import com.braintribe.model.processing.mpc.evaluator.api.MpcRegistry;

/**
 * The builder of the MPC context
 * 
 */
public interface MpcContextBuilder {

	/**
	 * Adds a {@link MpcRegistry} to be used for evaluation
	 * 
	 * @return A context builder with a registry
	 */
	MpcContextBuilder withRegistry(MpcRegistry registry);

	/**
	 * Matches a model path condition with a modelpathElement by using an
	 * MpcEvaluatorContext which delegates to it's matches method using the
	 * 
	 * experts of the registry for evaluation.
	 * If there is no match a null is returned, otherwise an instance of
	 * MpcMatch.
	 * 
	 * @param condition
	 *            ModelPathCondition that requires matching
	 * @param element
	 *            Path that will be used to evaluate the condition
	 * @return MpcMatch instance of the result or null
	 * @throws MpcEvaluatorRuntimeException
	 */
	MpcMatch mpcMatches(Object condition, IModelPathElement element) throws MpcEvaluatorRuntimeException;

}
