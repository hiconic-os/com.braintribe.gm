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
package com.braintribe.model.processing.vde.evaluator.impl.arithmetic;

import com.braintribe.model.processing.vde.evaluator.api.VdeRuntimeException;
import com.braintribe.model.processing.vde.evaluator.api.arithmetic.ArithmeticEvalExpert;

/**
 * An {@link ArithmeticEvalExpert} that flips the order of the operands
 * 
 * @param <T1>
 *            Type of first operand
 * @param <T2>
 *            Type of second operand
 */
public class InvertingArithmeticEvalExpert<T1, T2> implements ArithmeticEvalExpert<T1, T2> {

	private ArithmeticEvalExpert<T1, T2> expert;

	public InvertingArithmeticEvalExpert(ArithmeticEvalExpert<T1, T2> expert) {
		this.expert = expert;

	}

	@Override
	@SuppressWarnings("unchecked")
	public Object evaluate(T1 firstOperand, T2 secondOperand) throws VdeRuntimeException {
		return expert.evaluate((T1) secondOperand, (T2) firstOperand);
	}

}
