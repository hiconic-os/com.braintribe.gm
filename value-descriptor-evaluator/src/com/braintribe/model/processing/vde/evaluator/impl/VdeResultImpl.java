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
package com.braintribe.model.processing.vde.evaluator.impl;

import com.braintribe.model.processing.vde.evaluator.api.VdeResult;

/**
 * Implementation of VdeResult
 * 
 * @see VdeResult
 * 
 */
public class VdeResultImpl implements VdeResult {

	private Object result;
	private boolean volatileValue;
	private final boolean noEvaluationPossible;
	private String noEvaluationReason;

	/**
	 * This constructor assumes that no evaluation was possible for the VD in
	 * question.
	 */
	public VdeResultImpl(String noEvalutionReason) {
		this.noEvaluationPossible = true;
		this.noEvaluationReason = noEvalutionReason;
	}

	/**
	 * Creates a wrapper for the Vde. This constructor assumes that evaluation
	 * was possible for the invoker VD.
	 */
	public VdeResultImpl(Object result, boolean volatileValue) {
		this.result = result;
		this.volatileValue = volatileValue;
		this.noEvaluationPossible = false;
	}

	@Override
	public Object getResult() {
		return result;
	}

	@Override
	public boolean isVolatileValue() {
		return volatileValue;
	}

	@Override
	public boolean isNoEvaluationPossible() {
		return noEvaluationPossible;
	}

	@Override
	public String getNoEvaluationReason() {
		return noEvaluationReason;
	}

}
