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
package com.braintribe.model.processing.query.tools.traverse;

import java.util.function.Predicate;

import com.braintribe.model.query.Operand;
import com.braintribe.model.query.PropertyOperand;
import com.braintribe.model.query.Source;
import com.braintribe.model.query.functions.JoinFunction;
import com.braintribe.model.query.functions.Localize;
import com.braintribe.model.query.functions.QueryFunction;
import com.braintribe.model.query.functions.aggregate.AggregateFunction;

/**
 * 
 * @author peter.gazdik
 */
public class OperandTraverser {

	protected Predicate<Object> evalExcludedCheck;
	protected OperandVisitor operandVisitor;

	public static void traverse(Predicate<Object> evalExcludedCheck, OperandVisitor operandVisitor, Object operand) {
		if (operand instanceof Operand && !evalExcludedCheck.test(operand))
			new OperandTraverser(evalExcludedCheck, operandVisitor).traverseOperand(operand);
	}

	public OperandTraverser(Predicate<Object> evalExcludedCheck, OperandVisitor operandVisitor) {
		this.evalExcludedCheck = evalExcludedCheck;
		this.operandVisitor = operandVisitor;
	}

	public void traverseOperand(Object operand) {
		if (!(operand instanceof Operand) || evalExcludedCheck.test(operand)) {
			operandVisitor.visitStaticValue(operand);
			return;
		}

		if (operand instanceof PropertyOperand) {
			PropertyOperand po = (PropertyOperand) operand;
			if (po.getPropertyName() != null) {
				operandVisitor.visit(po);
			} else {
				operandVisitor.visit(po.getSource());
			}

		} else if (operand instanceof JoinFunction) {
			operandVisitor.visit((JoinFunction) operand);

		} else if (operand instanceof Localize) {
			operandVisitor.visit((Localize) operand);

		} else if (operand instanceof AggregateFunction) {
			operandVisitor.visit((AggregateFunction) operand);

		} else if (operand instanceof QueryFunction) {
			operandVisitor.visit((QueryFunction) operand);

		} else if (operand instanceof Source) {
			operandVisitor.visit((Source) operand);

		} else {
			throw new IllegalArgumentException("Unsupported operand: " + operand + " of type: " + operand.getClass().getName());
		}
	}
}
