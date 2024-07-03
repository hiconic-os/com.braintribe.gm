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
import com.braintribe.model.mpc.logic.MpcJunction;
import com.braintribe.model.mpc.logic.MpcJunctionCapture;
import com.braintribe.model.processing.mpc.evaluator.api.logic.MpcJunctionCaptureResult;

/**
 * This is used to encapsulate several methods pertaining to the evaluation the result of an MPC match according to
 * different {@link MpcJunctionCapture}.
 * 
 * Used by {@link MpcConjunctionEvaluator} and by {@link MpcDisjunctionEvaluator}
 * 
 * @param <C>
 *            Type of the ModelPathCondition, which should extend {@link MpcJunction}
 */
public interface MpcJunctionEvaluator<C extends MpcJunction> extends MpcEvaluator<C> {

	/**
	 * Computes the correct {@link MpcJunctionCaptureResult} based on the provided {@link MpcJunctionCapture}. It takes
	 * into consideration the current computed result and the result so far, where both are of type
	 * {@link MpcJunctionCaptureResult}. It also factors in if this is the first attempt for evaluation of the {@link MpcJunction}
	 * 
	 * @param capture
	 * 			{@link MpcJunctionCapture} that is provided by {@link MpcJunction}
	 * @param captureResult
	 * 			{@link MpcJunctionCapture} the result so far
	 * @param currentCaptureResult
	 * 			{@link MpcJunctionCapture} the result of the evaluation of the current evaluation
	 * @param firstEntry
	 * 			boolean that indicates if this is the first attempt to evaluate a result or not
	 * @return
	 * @throws MpcEvaluatorRuntimeException
	 */
	MpcJunctionCaptureResult evaluateMatchResult(MpcJunctionCapture capture, MpcJunctionCaptureResult captureResult,
			MpcJunctionCaptureResult currentCaptureResult, boolean firstEntry) throws MpcEvaluatorRuntimeException;

	/**
	 * Initialises a {@link MpcJunctionCaptureResult} based on the provided {@link MpcJunctionCapture}
	 * 
	 * @param capture
	 *            {@link MpcJunctionCapture} as provided in {@link MpcJunction}
	 * @param element
	 *            {@link IModelPathElement} as provided in {@link MpcJunction}
	 * @return
	 */
	MpcJunctionCaptureResult initResult(MpcJunctionCapture capture, IModelPathElement element);

}
