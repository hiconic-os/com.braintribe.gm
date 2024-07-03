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
package com.braintribe.model.processing.vde.evaluator.impl.predicate.greater;

import java.math.BigDecimal;

import com.braintribe.model.bvd.predicate.Greater;
import com.braintribe.model.processing.vde.evaluator.api.VdeRuntimeException;
import com.braintribe.model.processing.vde.evaluator.api.predicate.PredicateEvalExpert;

/**
 * Expert for {@link Greater} that operates on left hand side operand of type
 * Number and right hand side operand of type Number, internally they are
 * converted to BigDecimal
 * 
 */
public class DecimalNumericGreater implements PredicateEvalExpert<Number, Number> {

	private static DecimalNumericGreater instance = null;

	protected DecimalNumericGreater() {
		// empty
	}

	public static DecimalNumericGreater getInstance() {
		if (instance == null) {
			instance = new DecimalNumericGreater();
		}
		return instance;
	}

	@Override
	public Object evaluate(Number leftOperand, Number rightOperand) throws VdeRuntimeException {
		BigDecimal left = new BigDecimal(leftOperand.toString());
		BigDecimal right = new BigDecimal(rightOperand.toString());
		return left.compareTo(right) == 1.0 ? Boolean.TRUE : Boolean.FALSE;
	}

}
