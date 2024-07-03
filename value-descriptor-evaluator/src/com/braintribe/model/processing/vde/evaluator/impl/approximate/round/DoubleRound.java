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
package com.braintribe.model.processing.vde.evaluator.impl.approximate.round;

import com.braintribe.model.bvd.math.Round;
import com.braintribe.model.processing.vde.evaluator.api.VdeRuntimeException;
import com.braintribe.model.processing.vde.evaluator.api.approximate.ApproximateEvalExpert;
import com.braintribe.model.processing.vde.evaluator.impl.approximate.ApproximateVdeUtil;

/**
 * Expert for {@link Round} that operates on value of type Double and precision of type Number 
 *
 */
public class DoubleRound implements ApproximateEvalExpert<Double, Number> {

	private static DoubleRound instance = null;

	protected DoubleRound() {
		// empty
	}

	public static DoubleRound getInstance() {
		if (instance == null) {
			instance = new DoubleRound();
		}
		return instance;
	}
	
	@Override
	public Object evaluate(Double firstOperand, Number secondOperand) throws VdeRuntimeException {
		Double result = null;
		if (firstOperand >= 0){
			result = ApproximateVdeUtil.getRound(firstOperand, secondOperand).doubleValue();
		}
		else{
			result = -1.0 * ApproximateVdeUtil.getRound(-1.0*firstOperand, secondOperand, false).doubleValue();
		}
		return result;
	}

}
