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

import com.braintribe.logging.Logger;

/**
 * This is the state of an expert that can conduct multiple evaluations (e.g. MpcQuantiferEvaluator). This sate is
 * preserved within the scope of a nested condition expert.
 * 
 */
public class MpcMultiEvaluationState {

	private static Logger logger = Logger.getLogger(MpcMultiEvaluationState.class);
	private static boolean trace = logger.isTraceEnabled();
	
	/**
	 * Each state has a linked list structure to connect it with other evaluation states
	 */
	public MpcMultiEvaluationState next;
	public MpcMultiEvaluationState previous;

	/**
	 * States are defined by their potential match, from the previous iteration. If it is set to null, this indicates a
	 * fresh iteration will take place
	 */
	public MpcPotentialMatch potentialMatch;
	/**
	 * A flag to indicate if the evaluator related to this state will be invoked to resume evaluation (e.g.
	 * backtracking) or if it will maintain its previously computed state
	 */
	public boolean isActive;

	/**
	 * @return true iff there is a non-null potential match object that has the possibility to produce more results.
	 *         Otherwise, returns false
	 */
	public boolean hasAnotherProcessingAttempt() {
		if (trace) logger.trace("Check for possible evaluation attempts, where current potentialMatch:" + potentialMatch);
		if (potentialMatch != null) {
			return potentialMatch.hasAnotherAttempt();
		}
		return false;
	}

}
