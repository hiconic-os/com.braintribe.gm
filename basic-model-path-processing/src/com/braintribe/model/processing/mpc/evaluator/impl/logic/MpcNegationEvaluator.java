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
import com.braintribe.model.mpc.logic.MpcNegation;
import com.braintribe.model.processing.mpc.evaluator.api.MpcEvaluator;
import com.braintribe.model.processing.mpc.evaluator.api.MpcEvaluatorContext;
import com.braintribe.model.processing.mpc.evaluator.api.MpcEvaluatorRuntimeException;
import com.braintribe.model.processing.mpc.evaluator.api.MpcMatch;
import com.braintribe.model.processing.mpc.evaluator.impl.MpcMatchImpl;

/**
 * {@link MpcEvaluator} for {@link MpcNegation}
 * 
 */
public class MpcNegationEvaluator implements MpcEvaluator<MpcNegation> {

	private static Logger logger = Logger.getLogger(MpcNegationEvaluator.class);
	private static boolean debug = logger.isDebugEnabled();
	
	@Override
	public MpcMatch matches(MpcEvaluatorContext context, MpcNegation condition, IModelPathElement element) throws MpcEvaluatorRuntimeException {

		if(debug) logger.debug("Evaluate operand" );
		MpcMatch evaluationResult = context.matches(condition.getOperand(), element);

		if(debug) logger.debug("just return the negation of the result, original result:" + evaluationResult );
		if (evaluationResult == null) {

			MpcMatchImpl result = new MpcMatchImpl(element);
			return result;
		} else {

			return null;
		}

	}

	@Override
	public boolean allowsPotentialMatches(MpcNegation condition) {
		return false;
	}

	@Override
	public boolean hasNestedConditions(MpcNegation condition) {
		return true;
	}

}
