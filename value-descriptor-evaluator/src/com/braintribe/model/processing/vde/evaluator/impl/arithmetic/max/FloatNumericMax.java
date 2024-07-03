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
package com.braintribe.model.processing.vde.evaluator.impl.arithmetic.max;

import com.braintribe.model.bvd.math.Max;
import com.braintribe.model.processing.vde.evaluator.api.arithmetic.ArithmeticEvalExpert;

/**
 * Expert for {@link Max} that operates on first operand of type Number and
 * second operand of type Number, where internally they are converted to
 * Float
 * 
 */
public class FloatNumericMax implements ArithmeticEvalExpert<Number, Number> {

	private static FloatNumericMax instance = null;

	protected FloatNumericMax() {
		// empty
	}

	public static FloatNumericMax getInstance() {
		if (instance == null) {
			instance = new FloatNumericMax();
		}
		return instance;
	}
	
	@Override
	public Object evaluate(Number firstOperand, Number secondOperand) {
		Float firstValue = firstOperand.floatValue();
		Float secondValue = secondOperand.floatValue();
		if(firstValue >= secondValue){
			return firstOperand;
		}
		else{
			return secondOperand;
		}
	}

}
