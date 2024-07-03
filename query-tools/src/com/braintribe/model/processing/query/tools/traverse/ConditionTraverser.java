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

import com.braintribe.model.query.conditions.AbstractJunction;
import com.braintribe.model.query.conditions.Condition;
import com.braintribe.model.query.conditions.FulltextComparison;
import com.braintribe.model.query.conditions.Negation;
import com.braintribe.model.query.conditions.ValueComparison;

/**
 * 
 */
public class ConditionTraverser extends OperandTraverser {

	protected final ConditionVisitor conditionVisitor;

	public static void traverse(Predicate<Object> evalExcludedCheck, ConditionVisitor conditionVisitor, OperandVisitor operandVisitor,
			Condition condition) {

		if (condition != null)
			new ConditionTraverser(evalExcludedCheck, conditionVisitor, operandVisitor).traverse(condition);
	}

	public ConditionTraverser(Predicate<Object> evalExcludedCheck, ConditionVisitor conditionVisitor, OperandVisitor operandVisitor) {
		super(evalExcludedCheck, operandVisitor);

		this.conditionVisitor = conditionVisitor;
	}

	public void traverse(Condition condition) {
		switch (condition.conditionType()) {
			case conjunction:
			case disjunction:
				traverse((AbstractJunction) condition);
				return;
			case fulltextComparison:
				traverse((FulltextComparison) condition);
				return;
			case negation:
				traverse(((Negation) condition).getOperand());
				return;
			case valueComparison:
				traverse((ValueComparison) condition);
				return;
		}

		throw new IllegalArgumentException("Unsupported condition: " + condition + " of type: " + condition.conditionType());
	}

	private void traverse(AbstractJunction condition) {
		for (Condition operand : condition.getOperands())
			traverse(operand);
	}

	private void traverse(FulltextComparison condition) {
		if (conditionVisitor != null)
			conditionVisitor.visit(condition);
	}

	private void traverse(ValueComparison condition) {
		if (conditionVisitor == null || conditionVisitor.visit(condition)) {
			traverseOperand(condition.getLeftOperand());
			traverseOperand(condition.getRightOperand());
		}
	}

}
