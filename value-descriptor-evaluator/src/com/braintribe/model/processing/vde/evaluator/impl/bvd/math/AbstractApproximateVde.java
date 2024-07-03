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
package com.braintribe.model.processing.vde.evaluator.impl.bvd.math;

import com.braintribe.model.bvd.math.ApproximateOperation;
import com.braintribe.model.processing.vde.evaluator.api.VdeContext;
import com.braintribe.model.processing.vde.evaluator.api.VdeResult;
import com.braintribe.model.processing.vde.evaluator.api.VdeRuntimeException;
import com.braintribe.model.processing.vde.evaluator.api.approximate.ApproximateEvalContext;
import com.braintribe.model.processing.vde.evaluator.api.approximate.ApproximateOperator;
import com.braintribe.model.processing.vde.evaluator.impl.VdeResultImpl;
import com.braintribe.model.processing.vde.evaluator.impl.approximate.ApproximateEvalContextImpl;

public abstract class AbstractApproximateVde {

	protected VdeResult evaluate(VdeContext context, ApproximateOperation valueDescriptor, ApproximateOperator operator) throws VdeRuntimeException {

		Object value = context.evaluate(valueDescriptor.getValue());
		Object precision = context.evaluate(valueDescriptor.getPrecision());
		if (value == null || precision == null) { 
			throw new VdeRuntimeException("Value and Precision must be provided for Approximate operation");
		} else {
			ApproximateEvalContext approximateContext = new ApproximateEvalContextImpl();
			Object result = approximateContext.evaluate(value, precision, operator);
			
			return new VdeResultImpl(result, false);
		}
	}}
