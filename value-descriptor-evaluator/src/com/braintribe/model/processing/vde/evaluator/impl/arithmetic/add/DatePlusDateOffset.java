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
package com.braintribe.model.processing.vde.evaluator.impl.arithmetic.add;

import java.util.Date;

import com.braintribe.model.bvd.math.Add;
import com.braintribe.model.processing.vde.evaluator.api.VdeRuntimeException;
import com.braintribe.model.processing.vde.evaluator.api.arithmetic.ArithmeticEvalExpert;
import com.braintribe.model.processing.vde.evaluator.impl.arithmetic.ArithmeticVdeUtil;
import com.braintribe.model.time.DateOffset;

/**
 * Expert for {@link Add} that operates on first operand of type Date and second
 * operand of type DateOffset
 * 
 */
public class DatePlusDateOffset implements ArithmeticEvalExpert<Date, DateOffset> {

	private static DatePlusDateOffset instance = null;

	protected DatePlusDateOffset() {
		// empty
	}

	public static DatePlusDateOffset getInstance() {
		if (instance == null) {
			instance = new DatePlusDateOffset();
		}
		return instance;
	}

	@Override
	public Object evaluate(Date firstOperand, DateOffset secondOperand) throws VdeRuntimeException {
		return ArithmeticVdeUtil.addDateOffsetToDate(firstOperand, secondOperand, 1.0);
	}

}
