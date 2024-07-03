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
package com.braintribe.model.processing.mpc.evaluator.impl.structure;

import com.braintribe.logging.Logger;
import com.braintribe.model.generic.path.api.IModelPathElement;
import com.braintribe.model.mpc.structure.MpcSequence;
import com.braintribe.model.processing.mpc.evaluator.api.MpcEvaluator;
import com.braintribe.model.processing.mpc.evaluator.api.MpcEvaluatorContext;
import com.braintribe.model.processing.mpc.evaluator.api.MpcEvaluatorRuntimeException;
import com.braintribe.model.processing.mpc.evaluator.api.MpcMatch;
import com.braintribe.model.processing.mpc.evaluator.impl.MpcMatchImpl;

/**
 * {@link MpcEvaluator} for {@link MpcSequence}
 *
 */
public class MpcSequenceEvaluator implements MpcEvaluator<MpcSequence> {

	private static Logger logger = Logger.getLogger(MpcSequenceEvaluator.class);
	private static boolean debug = logger.isDebugEnabled();
	private static boolean trace = logger.isTraceEnabled();
	
	@Override
	public MpcMatch matches(MpcEvaluatorContext context, MpcSequence condition, IModelPathElement element)throws MpcEvaluatorRuntimeException {

		IModelPathElement currentPath = element;
		
		if (debug) logger.debug("iterate over all the items in the sequence, where all must match in order for condition to match");
		for (Object sequenceElement : condition.getElements()) {

			if (debug) logger.debug("sequence has failed as there is no path anymore");
			if (currentPath == null) {
				return null;
			}

			MpcMatch currentResult = context.matches(sequenceElement, currentPath);

			if (currentResult == null) {
				if (debug) logger.debug("sequence has failed as there is no match");
				return null;
			}

			
			if (trace) logger.trace("set path for the next iteration");
			currentPath = currentResult.getPath();
		}

		MpcMatchImpl result = new MpcMatchImpl(null);

		if (debug) logger.debug("none capture: " + condition.getNoneCapture());
		if (condition.getNoneCapture()) {
			if (trace) logger.trace("return the path as it is");
			result.setPath(element);
		} else {
			if (trace) logger.trace("return the remaining part of the path");
			result.setPath(currentPath);
		}
		return result;
	}

	@Override
	public boolean allowsPotentialMatches(MpcSequence condition) {
		return false;
	}

	@Override
	public boolean hasNestedConditions(MpcSequence condition) {
		return true;
	}
}
