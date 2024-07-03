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
package com.braintribe.model.processing.mpc.evaluator.impl.logic;

import com.braintribe.logging.Logger;
import com.braintribe.model.generic.path.api.IModelPathElement;
import com.braintribe.model.mpc.logic.MpcJunction;
import com.braintribe.model.mpc.logic.MpcJunctionCapture;
import com.braintribe.model.processing.mpc.evaluator.api.MpcEvaluatorRuntimeException;
import com.braintribe.model.processing.mpc.evaluator.api.MpcJunctionEvaluator;
import com.braintribe.model.processing.mpc.evaluator.api.logic.MpcJunctionCaptureResult;

/**
 * Abstract implementation of {@link MpcJunctionEvaluator}
 * 
 * @param <C>
 *            Type of the ModelPathCondition, which should extend {@link MpcJunction}
 * 
 */
public abstract class MpcJunctionEvaluatorImpl<C extends MpcJunction> implements MpcJunctionEvaluator<C> {

	private static Logger logger = Logger.getLogger(MpcJunctionEvaluatorImpl.class);
	private static boolean trace = logger.isTraceEnabled();
	
	@Override
	public MpcJunctionCaptureResultImpl initResult(MpcJunctionCapture capture, IModelPathElement element) {
		
		if (trace) logger.trace("initilaise MpcJunctionCaptureResult");
		MpcJunctionCaptureResultImpl result = getNewCaptureResult();

		result.setReturnPath(element);
		
		if (trace) logger.trace("If junction capture is longest then set depth to zero, otherwise set length to max length");
		result.setPathLength((capture == MpcJunctionCapture.longest) ? 0 : element.getDepth());
		if (trace) logger.trace("Path length is: " + result.getPathLength());
		
		return result;
	}

	@Override
	public MpcJunctionCaptureResult evaluateMatchResult(MpcJunctionCapture capture,MpcJunctionCaptureResult captureResult, MpcJunctionCaptureResult currentCaptureResult, boolean firstEntry) throws MpcEvaluatorRuntimeException {

		if (trace) logger.trace("Compute the result based on the consumption of the model path element of and the junction capture");
		switch (capture) {
			case shortest:
				if (trace) logger.trace("evaluate all then, return the shortest if all are successful");

				if (currentCaptureResult.getPathLength() <= captureResult.getPathLength()) {
					captureResult.setPathLength(currentCaptureResult.getPathLength());
					captureResult.setReturnPath(currentCaptureResult.getReturnPath());
				}

				break;
			case longest:
				if (trace) logger.trace("evaluate all then, return the longest if all are successful");
				if (currentCaptureResult.getPathLength() >= captureResult.getPathLength()) {
					captureResult.setPathLength(currentCaptureResult.getPathLength());
					captureResult.setReturnPath(currentCaptureResult.getReturnPath());
				}

				break;
			case last:
				if (trace) logger.trace("evaluate all then, return the last if all are successful");
				captureResult = currentCaptureResult;

				break;
			case first:
				if (trace) logger.trace("evaluate all then, return the first if all are successful");
				captureResult = (firstEntry == true) ? currentCaptureResult : captureResult;

				break;
			case none:
				if (trace) logger.trace("return original path");
				// the capture result object is already set to the initial element to avoid confusion
				// TODO maybe add some kind of validation here ?
				break;
			default:
				logger.error("Unsupported Junction Capture: " + capture);
				throw new MpcEvaluatorRuntimeException("Unsupported Junction Capture: " + capture);
		}

		return captureResult;
	}

	protected MpcJunctionCaptureResultImpl getNewCaptureResult() {
		return new MpcJunctionCaptureResultImpl();
	}

}
