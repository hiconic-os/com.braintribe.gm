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
package com.braintribe.model.processing.vde.evaluator.api;

/**
 * This is the context that handles all the VDE related actions.
 * 
 * It provides evaluation methods and aspect adjustment methods.
 * 
 */
public interface VdeContext {

	/**
	 * Evaluation method for value descriptor, invokes @
	 * {@link #evaluate(Object, boolean) } with a false
	 * 
	 * @param object
	 *            The value descriptor that will be evaluated.
	 * @return The evaluated object
	 * @throws VdeRuntimeException
	 * 
	 */
	<T> T evaluate(Object object) throws VdeRuntimeException;

	/**
	 * The evaluation method which evaluates a value descriptor and stores the
	 * result in a cache when non-volatile. If a non value descriptor is
	 * provided it will be returned as is.
	 * 
	 * @param object
	 *            The value descriptor that will be evaluated.
	 * @param volatileEvaluation
	 *            boolean that indicates if this evaluation is volatile
	 * @return The evaluated object
	 * @throws VdeRuntimeException
	 */
	<T> T evaluate(Object object, boolean volatileEvaluation) throws VdeRuntimeException;

	/**
	 * Retrieves a value for given aspect. The value may be <tt>null</tt>.
	 * 
	 * @param aspect
	 *            The aspect itself.
	 * @return The value associated with the aspect
	 */
	<T, A extends VdeContextAspect<T>> T get(Class<A> aspect);

	/**
	 * Adds a value for an aspect
	 * 
	 * @param aspect
	 * @param value
	 */
	<T, A extends VdeContextAspect<? super T>> void put(Class<A> aspect, T value);

	/**
	 * Setting the {@link VdeRegistry} that is used by the context
	 * 
	 * @param registry
	 */
	void setVdeRegistry(VdeRegistry registry);

	/**
	 * Retrieves the registry
	 * 
	 * @return {@link VdeRegistry} used by context
	 */
	VdeRegistry getVdeRegistry();

	/**
	 * Adjusts the context to the provided evaluation mode
	 * 
	 * @param mode
	 *            The target evaluation mode {@see VdeEvaluationMode}
	 */
	void setEvaluationMode(VdeEvaluationMode mode);

	/**
	 * Returns the evaluation mode used by the context
	 * 
	 * @return The target evaluation mode {@see VdeEvaluationMode}
	 */
	VdeEvaluationMode getEvaluationMode();
}
