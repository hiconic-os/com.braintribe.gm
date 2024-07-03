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
package com.braintribe.model.processing.query.stringifier.experts;

import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.processing.query.api.stringifier.QueryStringifierRuntimeException;
import com.braintribe.model.processing.query.api.stringifier.experts.Stringifier;
import com.braintribe.model.processing.query.stringifier.BasicQueryStringifierContext;
import com.braintribe.model.query.From;
import com.braintribe.model.query.Join;
import com.braintribe.model.query.Operand;
import com.braintribe.model.query.Operator;
import com.braintribe.model.query.PropertyOperand;
import com.braintribe.model.query.Source;
import com.braintribe.model.query.conditions.ValueComparison;

public class ValueComparisonStringifier implements Stringifier<ValueComparison, BasicQueryStringifierContext> {

	@Override
	public String stringify(ValueComparison vc, BasicQueryStringifierContext context) throws QueryStringifierRuntimeException {
		StringBuilder sb = new StringBuilder();

		// Get operator set equal as default operator
		Operator operator = vc.getOperator();
		operator = (operator == null ? Operator.equal : operator);

		// Stringify and add left, right operand and operator
		stringifyOperand(context, vc.getLeftOperand(), vc.getRightOperand(), sb);
		sb.append(" ");
		sb.append(Operator.getSignToOperator(operator));
		sb.append(" ");
		stringifyOperand(context, vc.getRightOperand(), vc.getLeftOperand(), sb);

		return sb.toString();
	}

	private void stringifyOperand(BasicQueryStringifierContext context, Object operand, Object otherOperand, StringBuilder sb) {
		if (context.hideConfidential() && isConfidentialProperty(context, otherOperand))
			sb.append("***");
		else
			context.stringifyAndAppend(operand, sb);
	}

	private boolean isConfidentialProperty(BasicQueryStringifierContext context, Object operand) {
		if (!(operand instanceof Operand))
			return false;

		if (operand instanceof From)
			return false;

		if (operand instanceof Join) {
			Join join = (Join) operand;
			return isConfidential(context, join.getSource(), join.getProperty());
		}

		if (operand instanceof PropertyOperand) {
			PropertyOperand po = (PropertyOperand) operand;

			if (po.getPropertyName() == null)
				return isConfidentialProperty(context, po.getSource());
			else
				return isConfidential(context, po.getSource(), po.getPropertyName());
		}

		return false;
	}

	private boolean isConfidential(BasicQueryStringifierContext context, Source source, String propertyPath) {
		Property p = PropertyResolver.resolveProperty(source, propertyPath, context.peekDefaultSourceType());

		return p != null && p.isConfidential();
	}
}
