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

import com.braintribe.model.bvd.predicate.Greater;
import com.braintribe.model.processing.vde.evaluator.api.VdeRuntimeException;
import com.braintribe.model.processing.vde.evaluator.api.predicate.PredicateEvalExpert;

/**
 * Expert for {@link Greater} that operates on left hand side operand of type
 * Number and right hand side operand of type Number, internally they are
 * converted to double
 * 
 */
public class NumberGreater implements PredicateEvalExpert<Number, Number> {

	private static NumberGreater instance = null;

	protected NumberGreater() {
		// empty
	}

	public static NumberGreater getInstance() {
		if (instance == null) {
			instance = new NumberGreater();
		}
		return instance;
	}
	
	@Override
	public Object evaluate(Number leftOperand, Number rightOperand) throws VdeRuntimeException {
		double left = leftOperand.doubleValue();
		double right = rightOperand.doubleValue();
		return left > right;
	}

}
