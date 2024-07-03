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
package com.braintribe.model.processing.vde.evaluator.impl.bvd.predicate;

import com.braintribe.model.bvd.predicate.BinaryPredicate;
import com.braintribe.model.processing.vde.evaluator.api.VdeContext;
import com.braintribe.model.processing.vde.evaluator.api.VdeResult;
import com.braintribe.model.processing.vde.evaluator.api.VdeRuntimeException;
import com.braintribe.model.processing.vde.evaluator.api.predicate.PredicateEvalContext;
import com.braintribe.model.processing.vde.evaluator.api.predicate.PredicateOperator;
import com.braintribe.model.processing.vde.evaluator.impl.VdeResultImpl;
import com.braintribe.model.processing.vde.evaluator.impl.predicate.PredicateEvalContextImpl;

public abstract class AbstractPredicateVde {

	protected VdeResult evaluate(VdeContext context, BinaryPredicate valueDescriptor, PredicateOperator operator) throws VdeRuntimeException {

		Object left = context.evaluate(valueDescriptor.getLeftOperand());
		Object right = context.evaluate(valueDescriptor.getRightOperand());
		if (left == null || right == null) { 
			throw new VdeRuntimeException("Left and right operands must be provided for Predicate operation");
		} else {
			PredicateEvalContext predicateContext = new PredicateEvalContextImpl();
			Object result = predicateContext.evaluate(left, right, operator);
			
			return new VdeResultImpl(result, false);
		}
	}}
