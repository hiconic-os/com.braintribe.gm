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

import java.math.BigDecimal;

import com.braintribe.model.bvd.math.Round;
import com.braintribe.model.processing.vde.evaluator.api.VdeRuntimeException;
import com.braintribe.model.processing.vde.evaluator.api.approximate.ApproximateEvalExpert;
import com.braintribe.model.processing.vde.evaluator.impl.approximate.ApproximateVdeUtil;

/**
 * Expert for {@link Round} that operates on value of type BigDecimal and precision of type Number 
 *
 */
public class DecimalRound implements ApproximateEvalExpert<BigDecimal, Number> {

	private static DecimalRound instance = null;

	protected DecimalRound() {
		// empty
	}

	public static DecimalRound getInstance() {
		if (instance == null) {
			instance = new DecimalRound();
		}
		return instance;
	}
	
	@Override
	public Object evaluate(BigDecimal firstOperand, Number secondOperand) throws VdeRuntimeException {
		BigDecimal result = null;
		if (firstOperand.compareTo(new BigDecimal(0.0)) >= 0.0){
			result = ApproximateVdeUtil.getDecimalRound(firstOperand, secondOperand);
		}
		else{
			BigDecimal negativeOne = new BigDecimal(-1.0);
			result = ApproximateVdeUtil.getDecimalRound(firstOperand.multiply(negativeOne), secondOperand, false).multiply(negativeOne);
		}
		return result.stripTrailingZeros();
	}

}
