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
 * An expert that will evaluate a {@link BinaryPredicate} between two
 * operands of type E and T
 * 
 * @param <E>
 *            Type of first operand
 * @param <T>
 *            Type of second operand
 */
public interface PredicateEvalExpert<E, T> {

	Object evaluate(E leftOperand, T rightOperand) throws VdeRuntimeException;

}
