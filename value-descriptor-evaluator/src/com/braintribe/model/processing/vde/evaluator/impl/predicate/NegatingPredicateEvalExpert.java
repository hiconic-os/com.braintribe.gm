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
package com.braintribe.model.processing.vde.evaluator.impl.predicate;

import com.braintribe.model.processing.vde.evaluator.api.VdeRuntimeException;
import com.braintribe.model.processing.vde.evaluator.api.predicate.PredicateEvalExpert;

/**
 * An {@link PredicateEvalExpert} that negates a result prior to returning the value
 * 
 * @param <T1>
 *            Type of first operand
 * @param <T2>
 *            Type of second operand
 */
public class NegatingPredicateEvalExpert<T1, T2> implements PredicateEvalExpert<T1, T2> {

	private PredicateEvalExpert<T1, T2> expert;

	public NegatingPredicateEvalExpert(PredicateEvalExpert<T1,T2> expert) {
		this.expert = expert;
		
	}
	
	@Override
	public Object evaluate(T1 firstOperand, T2 secondOperand) throws VdeRuntimeException {
		Object result = expert.evaluate(firstOperand, secondOperand);
		return ! ((Boolean)result);
	}

}
