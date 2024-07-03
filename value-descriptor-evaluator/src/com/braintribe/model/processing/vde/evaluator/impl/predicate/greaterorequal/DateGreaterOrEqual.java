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
package com.braintribe.model.processing.vde.evaluator.impl.predicate.greaterorequal;

import java.util.Date;

import com.braintribe.model.bvd.predicate.GreaterOrEqual;
import com.braintribe.model.processing.vde.evaluator.api.VdeRuntimeException;
import com.braintribe.model.processing.vde.evaluator.api.predicate.PredicateEvalExpert;

/**
 * Expert for {@link GreaterOrEqual} that operates on left hand side operand of type
 * Date and right hand side operand of type Date
 * 
 */
public class DateGreaterOrEqual implements PredicateEvalExpert<Date, Date> {

	private static DateGreaterOrEqual instance = null;

	protected DateGreaterOrEqual() {
		// empty
	}

	public static DateGreaterOrEqual getInstance() {
		if (instance == null) {
			instance = new DateGreaterOrEqual();
		}
		return instance;
	}
	
	@Override
	public Object evaluate(Date leftOperand, Date rightOperand) throws VdeRuntimeException {
		long left = leftOperand.getTime();
		long right = rightOperand.getTime();
		return left >= right;
	}

}
