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

/**
 * Scope of nested condition expert, it is used to contain all evaluation states within a nested conidtion expert
 * 
 */
public interface MpcNestedConditionExpertScope {
	
	/**
	 * Resets the tracking cursor of the multi evaluation experts within the scope of this instance
	 */
	void resetIteration();

	/**
	 * Increments the index for multi evaluation experts within the scope
	 */
	void incrementMultiEvaluationExpertIndex();

	/**
	 * Checks if the current expert is allowed to resume operation or not
	 * 
	 * @return true if the expert is allows to resume operations
	 */
	boolean isCurrentMultiEvaluationExpertActive();

	/**
	 * Checks if the current expert is new or there is an existing state
	 * 
	 * @return true if the expert has no preserved states with respect to the scope ( a previous state might have been
	 *         internally nullified to allow for all path exploration)
	 */
	boolean isCurrentMultiEvaluationExpertNew();

	/**
	 * Retrieves the state of the current multiple evaluation expert
	 * 
	 * @return {@link MpcPotentialMatch} representing the state of the expert
	 */
	MpcPotentialMatch getMultiEvaluationExpertState();

	/**
	 * Sets the state for the current multiple evaluation expert.
	 * 
	 * @param potentialMatch
	 *            The value of the state that should be preserved
	 */
	void setMultiEvaluationExpertState(MpcPotentialMatch potentialMatch);

	/**
	 * boolean indicating if there are unexplored paths from the perspective of the scope as a whole
	 * 
	 * @return true, if at least one preserved state allows has the potential to provide more matches
	 */
	boolean isUnexploredPathAvailable();
}
