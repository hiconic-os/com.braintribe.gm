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
package com.braintribe.model.processing.mpc;

import com.braintribe.model.generic.path.api.IModelPathElement;
import com.braintribe.model.mpc.ModelPathCondition;
import com.braintribe.model.processing.mp.builder.api.MpBuilder;
import com.braintribe.model.processing.mp.builder.impl.MpBuilderImpl;
import com.braintribe.model.processing.mpc.builder.api.MpcBuilder;
import com.braintribe.model.processing.mpc.builder.impl.MpcBuilderImpl;
import com.braintribe.model.processing.mpc.evaluator.api.MpcEvaluatorContext;
import com.braintribe.model.processing.mpc.evaluator.api.MpcEvaluatorRuntimeException;
import com.braintribe.model.processing.mpc.evaluator.api.MpcMatch;
import com.braintribe.model.processing.mpc.evaluator.api.builder.MpcContextBuilder;
import com.braintribe.model.processing.mpc.evaluator.api.builder.MpcRegistryBuilder;
import com.braintribe.model.processing.mpc.evaluator.impl.MpcEvaluatorContextImpl;
import com.braintribe.model.processing.mpc.evaluator.impl.builder.MpcRegistryBuilderImpl;

/**
 * Main Access point to all Model Path condition actions.
 * 
 * All methods are declared as static.
 * 
 */
public class MPC {

	/**
	 * Matches a model path condition with a modelpathElement, with the respect
	 * to a Context (In theory, the context will include all the preset values
	 * in a default context, and optionally more), invokes
	 * {@link #mpcMatches(ModelPathCondition, IModelPathElement)} Return true
	 * iff the result of the matching is not null
	 * 
	 * @param condition
	 *            ModelPathCondition that requires matching
	 * @param element
	 *            Path that will be used to evaluate the condition
	 * @return boolean indicating if there was a match
	 * @throws MpcEvaluatorRuntimeException
	 */
	public static Object evaluate(Object condition, IModelPathElement element, MpcContextBuilder contextBuilder) throws MpcEvaluatorRuntimeException {
		return contextBuilder.mpcMatches(condition, element);
	}

	/**
	 * Matches a model path condition with a modelpathElement, invokes
	 * {@link #mpcMatches(ModelPathCondition, IModelPathElement)} Return true
	 * iff the result of the matching is not null
	 * 
	 * @param condition
	 *            ModelPathCondition that requires matching
	 * @param element
	 *            Path that will be used to evaluate the condition
	 * @return boolean indicating if there was a match
	 * @throws MpcEvaluatorRuntimeException
	 */
	public static boolean matches(Object condition, IModelPathElement element) throws MpcEvaluatorRuntimeException {

		MpcMatch result = mpcMatches(condition, element);

		if (result == null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Matches a model path condition with a modelpathElement by using an
	 * MpcEvaluatorContext which delegates to it's matches method.
	 * 
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
	public static MpcMatch mpcMatches(Object condition, IModelPathElement element) throws MpcEvaluatorRuntimeException {

		if (condition == null) {
			throw new MpcEvaluatorRuntimeException("MPC condition can not be null");
		}
		MpcEvaluatorContext context = new MpcEvaluatorContextImpl();
		return context.matches(condition, element);
	}

	/**
	 * Instantiates an {@link MpcBuilder}
	 * 
	 * @return
	 */
	public static MpcBuilder builder() {
		return new MpcBuilderImpl();
	}
	
	/**
	 * Instantiates an {@link MpBuilder}
	 * 
	 * @return
	 */
	public static MpBuilder mpBuilder() {
		return new MpBuilderImpl();
	}

	/**
	 * @return A new instance of {@link MpcRegistryBuilder}
	 */
	public static MpcRegistryBuilder registryBuilder() {
		return new MpcRegistryBuilderImpl();
	}
}
