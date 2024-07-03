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

import static com.braintribe.utils.lcd.CollectionTools2.isEmpty;

import java.util.Iterator;
import java.util.List;

import com.braintribe.model.bvd.math.ArithmeticOperation;
import com.braintribe.model.processing.vde.evaluator.api.VdeContext;
import com.braintribe.model.processing.vde.evaluator.api.VdeResult;
import com.braintribe.model.processing.vde.evaluator.api.VdeRuntimeException;
import com.braintribe.model.processing.vde.evaluator.api.arithmetic.ArithmeticOperator;
import com.braintribe.model.processing.vde.evaluator.impl.VdeResultImpl;
import com.braintribe.model.processing.vde.evaluator.impl.arithmetic.ArithmeticOperatorEval;

public abstract class AbstractArithmeticVde {

	protected VdeResult evaluate(VdeContext context, ArithmeticOperation valueDescriptor, ArithmeticOperator operator) throws VdeRuntimeException {

		List<Object> operandList = valueDescriptor.getOperands();

		if (isEmpty(operandList)) {
			throw new VdeRuntimeException("No operands provided for Arithmetic operation");
		}

		Object result = evaluateOperands(context, operandList, operator);
		return new VdeResultImpl(result, false);
	}

	private Object evaluateOperands(VdeContext context, List<Object> operandList, ArithmeticOperator operator) {
		if (operandList.size() == 1) {
			return context.evaluate(operandList.get(0));
		}

		Iterator<Object> it = operandList.iterator();
		Object left = context.evaluate(it.next());

		while (it.hasNext()) {
			Object right = context.evaluate(it.next());
			left = ArithmeticOperatorEval.evaluate(left, right, operator);
		}

		return left;
	}

}
