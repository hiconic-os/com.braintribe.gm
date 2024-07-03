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

import java.util.ArrayList;
import java.util.List;

import com.braintribe.model.bvd.math.Avg;
import com.braintribe.model.processing.vde.evaluator.api.ValueDescriptorEvaluator;
import com.braintribe.model.processing.vde.evaluator.api.VdeContext;
import com.braintribe.model.processing.vde.evaluator.api.VdeResult;
import com.braintribe.model.processing.vde.evaluator.api.VdeRuntimeException;
import com.braintribe.model.processing.vde.evaluator.api.arithmetic.ArithmeticOperator;

/**
 * {@link ValueDescriptorEvaluator} for {@link Avg}
 * 
 */
public class AvgVde extends AbstractArithmeticVde implements ValueDescriptorEvaluator<Avg> {

	@Override
	public VdeResult evaluate(VdeContext context, Avg valueDescriptor) throws VdeRuntimeException {

		List<Object> operandsList = valueDescriptor.getOperands();

		if (operandsList == null || operandsList.isEmpty()) {
			throw new VdeRuntimeException("No operands provided for Arithmetic average operation");
		} else {

			int size = operandsList.size();
			Object result = super.evaluate(context, valueDescriptor, ArithmeticOperator.plus).getResult();

			List<Object> averageOperands = new ArrayList<Object>();
			averageOperands.add(result);
			averageOperands.add(Double.valueOf(size));

			valueDescriptor.setOperands(averageOperands);
			return super.evaluate(context, valueDescriptor, ArithmeticOperator.divide);
		}
	}
}
