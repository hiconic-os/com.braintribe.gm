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
package com.braintribe.model.processing.mpc.evaluator.api;

import com.braintribe.model.generic.path.api.IModelPathElement;
import com.braintribe.model.mpc.ModelPathCondition;
import com.braintribe.model.processing.mpc.evaluator.api.multievaluation.MpcNestedConditionExpertScope;
import com.braintribe.model.processing.mpc.evaluator.api.multievaluation.MpcPotentialMatch;

/**
 * The context that is used by all {@link MpcEvaluator}
 */
public interface MpcEvaluatorContext {

	/**
	 * Setting the {@link MpcRegistry} that is used by the context
	 * 
	 * @param registry
	 */
	void setMpcRegistry(MpcRegistry registry);

	/**
	 * Retrieves the registry
	 * 
	 * @return {@link MpcRegistry} used by context
	 */
	MpcRegistry getMpcRegistry();

	/**
	 * Evaluates given conditions against given {@link IModelPathElement}.
	 * Basically this finds the expert corresponding to the type of given
	 * {@link ModelPathCondition} and uses it's <tt>matches</tt> method.
	 * 
	 * @return the result of the
	 *         {@link MpcEvaluator#matches(MpcEvaluatorContext, ModelPathCondition, IModelPathElement)}
	 *         method of the corresponding {@link MpcEvaluator}.
	 * @throws MpcEvaluatorRuntimeException
	 */
	MpcMatch matches(Object condition, IModelPathElement element) throws MpcEvaluatorRuntimeException;

	/**
	 * Retrieves the preserved state for {@link MpcEvaluator} within the current
	 * {@link MpcNestedConditionExpertScope} scope.
	 * 
	 * @param expert
	 *            {@link MpcEvaluator} that requests the state
	 * @param condition
	 *            {@link ModelPathCondition} that is associated with the
	 *            {@link MpcEvaluator} expert
	 * @return {@link MpcPotentialMatch} that represents the state of the
	 *         {@link MpcEvaluator}, null if no valid preserved state exists
	 */
	<M extends MpcPotentialMatch, C extends ModelPathCondition> M getPreservedState(MpcEvaluator<C> expert, C condition);

}
