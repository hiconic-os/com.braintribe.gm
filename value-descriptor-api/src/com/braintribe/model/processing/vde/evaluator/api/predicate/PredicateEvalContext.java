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
package com.braintribe.model.processing.vde.evaluator.api.predicate;

import com.braintribe.model.bvd.predicate.BinaryPredicate;
import com.braintribe.model.processing.vde.evaluator.api.VdeRuntimeException;

/**
 * Context that is responsible for evaluation of all possible
 * {@link BinaryPredicate} operations. It identifies the correct implementation of the
 * {@link BinaryPredicate} that adheres to the types of the provided
 * operands and returns the result of that implementation
 * 
 */
public interface PredicateEvalContext {

	/**
	 * Evaluates the operands according to the {@link PredicateOperator}. It
	 * selects the correct {@link PredicateEvalExpert} implementation that
	 * satisfies the types of the operands and the given operation
	 * 
	 * @param firstOperand
	 *            The left hand side of the operation
	 * @param secondOperand
	 *            The right hand side of the operation
	 * @param operation
	 *            The type of binary operation
	 * @return result of binary operation with the two operands
	 * @throws VdeRuntimeException
	 */
	<T> T evaluate(Object leftOperand, Object rightOperand, PredicateOperator operation) throws VdeRuntimeException;
	
	
}
