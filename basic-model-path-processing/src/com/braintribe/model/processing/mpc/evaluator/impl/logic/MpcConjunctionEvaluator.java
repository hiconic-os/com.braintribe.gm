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
import com.braintribe.model.mpc.logic.MpcConjunction;
import com.braintribe.model.processing.mpc.evaluator.api.MpcEvaluator;
import com.braintribe.model.processing.mpc.evaluator.api.MpcEvaluatorContext;
import com.braintribe.model.processing.mpc.evaluator.api.MpcEvaluatorRuntimeException;
import com.braintribe.model.processing.mpc.evaluator.api.MpcMatch;

/**
 * {@link MpcEvaluator} for {@link MpcConjunction}
 * 
 */
public class MpcConjunctionEvaluator extends MpcJunctionEvaluatorImpl<MpcConjunction> {

	private static Logger logger = Logger.getLogger(MpcConjunctionEvaluator.class);
	private static boolean debug = logger.isDebugEnabled();
	private static boolean trace = logger.isTraceEnabled();
	
	@Override
	public MpcMatch matches(MpcEvaluatorContext context, MpcConjunction condition, IModelPathElement element) throws MpcEvaluatorRuntimeException {

		if(trace) logger.trace("init values to assist in identifying the actual output that will be used by this method.");
		MpcJunctionCaptureResultImpl result = initResult(condition.getJunctionCapture(), element);
		boolean firstEntry = true;

		if(debug) logger.debug("loop through all the operands");
		for (Object operand : condition.getOperands()) {

			if(debug) logger.debug("evaluate each operand");
			MpcJunctionCaptureResultImpl currentResult = getNewCaptureResult();
			currentResult.setReturnPath(context.matches(operand, element));

			if(trace) logger.trace("if one operand fails then return null, as this is the false case of the conjunction second part of the condition to cover the root case " + currentResult.getReturnPath() + " " + currentResult.getPathLength());
			if (currentResult.getReturnPath() == null && currentResult.getPathLength() != -1) {
				if(debug) logger.debug("no match, return null");
				return null;
			}

			if(trace) logger.trace("evaluate the return value according to the junction capture");
			result = (MpcJunctionCaptureResultImpl) evaluateMatchResult(condition.getJunctionCapture(), result, currentResult, firstEntry);

			if(trace) logger.trace("Set firstEntry to false as at least one operand has been evaluated");
			firstEntry = false;

		}

		if(debug) logger.debug("return: " + result.getMatchResult());
		return result.getMatchResult();
	}

	@Override
	public boolean allowsPotentialMatches(MpcConjunction condition) {
		return false;
	}

	@Override
	public boolean hasNestedConditions(MpcConjunction condition) {
		return true;
	}

}
