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
package com.braintribe.model.processing.vde.evaluator.impl.arithmetic.divide;

import com.braintribe.model.bvd.math.Divide;
import com.braintribe.model.processing.vde.evaluator.api.VdeRuntimeException;
import com.braintribe.model.processing.vde.evaluator.api.arithmetic.ArithmeticEvalExpert;
import com.braintribe.model.processing.vde.evaluator.impl.arithmetic.ArithmeticVdeUtil;
import com.braintribe.model.time.TimeSpan;

/**
 * Expert for {@link Divide} that operates on first operand of type TimeSpan and second
 * operand of type Number
 * 
 */
public class TimeSpanDivideNumber implements ArithmeticEvalExpert<TimeSpan, Number> {
	
	private static TimeSpanDivideNumber instance = null;

	protected TimeSpanDivideNumber() {
		// empty
	}

	public static TimeSpanDivideNumber getInstance() {
		if (instance == null) {
			instance = new TimeSpanDivideNumber();
		}
		return instance;
	}
	
	@Override
	public Object evaluate(TimeSpan firstOperand, Number secondOperand) throws VdeRuntimeException {
		double computationResult = ArithmeticVdeUtil.getNanoSeconds(firstOperand) / secondOperand.doubleValue();
		firstOperand.setValue(ArithmeticVdeUtil.convertNanoSeconds(firstOperand.getUnit(), computationResult));
		return firstOperand;
	}

}
