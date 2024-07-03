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

import java.util.Map;

import com.braintribe.model.mpc.ModelPathCondition;

/**
 * This is a registry that allows the evaluation of {@see ModelPathCondition}s.
 * It is comprised of a maps of Experts, where each expert is a MPC expert that
 * matches the provided ModelPathCondition directly.
 */
public interface MpcRegistry {

	/**
	 * @return A map of all experts, where the key is a ModelPathCondition
	 *         class and the value is the MPC expert
	 */
	Map<Class<? extends ModelPathCondition>, MpcEvaluator<?>> getExperts();

	/**
	 * Sets the expert maps in the registry
	 * 
	 * @param experts
	 *            A map where key is a ModelPathCondition class and the value is
	 *            the MPC expert
	 */
	void setExperts(Map<Class<? extends ModelPathCondition>, MpcEvaluator<?>> experts);

	/**
	 * Augment the registry with an expert
	 * 
	 * @param mpcType
	 *            type of ModelPathCondition
	 * @param mpcEvaluator
	 *            MPC Expert for ModelPathCondition
	 */
	<D extends ModelPathCondition> void putExpert(Class<D> mpcType, MpcEvaluator<? super D> mpcEvaluator);

	/**
	 * Remove an expert from the registry if it exists
	 * 
	 * @param mpcType
	 *            The type of ModelPathCondition
	 */
	void removeExpert(Class<? extends ModelPathCondition> mpcType);

	/**
	 * Reset all the experts and cache
	 */
	void resetRegistry();

	/**
	 * Merge the content of another registry into the existing content of this
	 * registry
	 * 
	 * @param otherRegistry
	 *            An external registry that will be merged with the current
	 *            content
	 */
	void loadOtherRegistry(MpcRegistry otherRegistry);

}
