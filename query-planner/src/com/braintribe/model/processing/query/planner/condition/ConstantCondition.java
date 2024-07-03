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
package com.braintribe.model.processing.query.planner.condition;

import com.braintribe.model.generic.pseudo.GenericEntity_pseudo;
import com.braintribe.model.query.Operator;
import com.braintribe.model.query.conditions.ConditionType;
import com.braintribe.model.query.conditions.ValueComparison;

/**
 * 
 */
public class ConstantCondition extends GenericEntity_pseudo implements ValueComparison {

	public static final ConstantCondition TRUE = instance(0, 0);
	public static final ConstantCondition FALSE = instance(1, 0);

	private static ConstantCondition instance(Integer left, Integer right) {
		ConstantCondition result = new ConstantCondition();
		result.setLeftOperand(left);
		result.setRightOperand(right);
		result.setOperator(Operator.equal);

		return result;
	}

	public static ConstantCondition instance(boolean isTrue) {
		return isTrue ? TRUE : FALSE;
	}

	public ConstantCondition negate() {
		return this == TRUE ? FALSE : TRUE;
	}

	// ##################################################
	// ## . . . . . . Actual Implementation. . . . . . ##
	// ##################################################

	private Operator operator;
	private Object leftOperand;
	private Object rightOperand;

	@Override
	public Operator getOperator() {
		return operator;
	}

	@Override
	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	@Override
	public Object getLeftOperand() {
		return leftOperand;
	}

	@Override
	public void setLeftOperand(Object leftOperand) {
		this.leftOperand = leftOperand;
	}

	@Override
	public Object getRightOperand() {
		return rightOperand;
	}

	@Override
	public void setRightOperand(Object rightOperand) {
		this.rightOperand = rightOperand;
	}

	@Override
	public ConditionType conditionType() {
		return ConditionType.valueComparison;
	}
}
