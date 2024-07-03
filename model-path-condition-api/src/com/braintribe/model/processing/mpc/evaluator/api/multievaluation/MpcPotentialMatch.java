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
package com.braintribe.model.processing.mpc.evaluator.api.multievaluation;

import com.braintribe.model.processing.mpc.evaluator.api.MpcMatch;

/**
 * A sub class of {@link MpcMatch} where it represents a valid {@link MpcMatch} with the possibility of further
 * processing.
 * 
 */
public interface MpcPotentialMatch extends MpcMatch {

	/**
	 * Retrieves {@link MpcEvaluationResumptionStrategy} that is relative to this match
	 * @return applicable {@link MpcEvaluationResumptionStrategy}
	 */
	MpcEvaluationResumptionStrategy getResumptionStrategy();

	/**
	 * @param strategy
	 * 			{@link MpcEvaluationResumptionStrategy} that should be applied to this match
	 */
	void setResumptionStrategy(MpcEvaluationResumptionStrategy strategy);

	/**
	 * @return true iff this match has more possible solutions 
	 */
	boolean hasAnotherAttempt();

}
