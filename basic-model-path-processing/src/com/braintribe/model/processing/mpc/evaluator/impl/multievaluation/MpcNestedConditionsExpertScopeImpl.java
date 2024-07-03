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
package com.braintribe.model.processing.mpc.evaluator.impl.multievaluation;

import com.braintribe.logging.Logger;
import com.braintribe.model.processing.mpc.evaluator.api.multievaluation.MpcMultiEvaluationState;
import com.braintribe.model.processing.mpc.evaluator.api.multievaluation.MpcNestedConditionExpertScope;
import com.braintribe.model.processing.mpc.evaluator.api.multievaluation.MpcPotentialMatch;

/**
 * Linked List implementation of {@link MpcNestedConditionExpertScope}
 */
public class MpcNestedConditionsExpertScopeImpl implements MpcNestedConditionExpertScope {

	private static Logger logger = Logger.getLogger(MpcNestedConditionsExpertScopeImpl.class);
	private static boolean trace = logger.isTraceEnabled();

	
	MpcMultiEvaluationState anchorBacktrackingState;
	MpcMultiEvaluationState currentBacktrackingState;
	
	
	public MpcNestedConditionsExpertScopeImpl next;
	public MpcNestedConditionsExpertScopeImpl previous;
	public int length;
	
	// TODO maybe add optimisation to handle the case of saving the results when it is not needed to continue
	
	public MpcNestedConditionsExpertScopeImpl(){
		anchorBacktrackingState = new MpcMultiEvaluationState();
		currentBacktrackingState = anchorBacktrackingState;
	}
	

	@Override
	public void incrementMultiEvaluationExpertIndex() {
		if (currentBacktrackingState.next == null) {	
			MpcMultiEvaluationState tempState = new MpcMultiEvaluationState();
			tempState.previous = currentBacktrackingState;
			currentBacktrackingState.next = tempState;
		}
		
		currentBacktrackingState = currentBacktrackingState.next;
	}

	@Override
	public boolean isCurrentMultiEvaluationExpertActive() {
		return currentBacktrackingState.isActive;
	}

	@Override
	public boolean isCurrentMultiEvaluationExpertNew() {
		if(currentBacktrackingState.potentialMatch == null){
			return true;
		}
		return false;
	}

	@Override
	public MpcPotentialMatch getMultiEvaluationExpertState() {		
		return currentBacktrackingState.potentialMatch;
	}

	@Override
	public void setMultiEvaluationExpertState(MpcPotentialMatch potentialMatch) {
		currentBacktrackingState.potentialMatch = potentialMatch;
	}

	@Override
	public boolean isUnexploredPathAvailable() {
		MpcMultiEvaluationState currentState = getLastStateAndResetActiveForAll();
		
		boolean result = false;
		if(trace) logger.trace("loop through all");
		while (currentState != null){
			if(!result){
				if(trace) logger.trace("currentState possible:" + currentState.hasAnotherProcessingAttempt());
				if(currentState.hasAnotherProcessingAttempt()){
					currentState.isActive = true;
					result = true;
					break;
				}
				else{
					logger.trace("reset value as it will have new data");
					currentState.potentialMatch = null;
				}
			}
			
			currentState = currentState.previous;
		}
		return result;
	}
	
	private MpcMultiEvaluationState getLastStateAndResetActiveForAll(){
		MpcMultiEvaluationState currentState = anchorBacktrackingState.next;
		MpcMultiEvaluationState lastState = anchorBacktrackingState.next;
		if(trace) logger.trace("Identify last state");
		while(currentState != null){
			currentState.isActive = false;
			if(currentState.next == null){
				lastState = currentState;
			}
			currentState = currentState.next;
		}
		
		return lastState;
	}
	
	@Override
	public void resetIteration() {
		currentBacktrackingState = anchorBacktrackingState;
	}

}
