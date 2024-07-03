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

import java.util.List;

import com.braintribe.model.processing.query.api.stringifier.QueryStringifierRuntimeException;
import com.braintribe.model.processing.query.api.stringifier.experts.Stringifier;
import com.braintribe.model.processing.query.stringifier.BasicQueryStringifierContext;
import com.braintribe.model.query.conditions.AbstractJunction;
import com.braintribe.model.query.conditions.Condition;
import com.braintribe.model.query.conditions.Disjunction;

public class JunctionStringifier<J extends AbstractJunction> implements Stringifier<J, BasicQueryStringifierContext> {
	@Override
	public String stringify(J operand, BasicQueryStringifierContext context) throws QueryStringifierRuntimeException {
		StringBuilder queryString = new StringBuilder();
		queryString.append("(");

		List<Condition> operands = operand.getOperands();
		if (operands != null) {
			// Work through all operands
			for (int i = 0, l = operands.size(); i < l; i++) {
				if (i > 0) {
					// Add junction delimiter
					queryString.append(" ");
					queryString.append((operand instanceof Disjunction) ? "or" : "and");
					queryString.append(" ");
				}

				// Add junctions operands to queryString
				context.stringifyAndAppend(operands.get(i), queryString);
			}
		}

		queryString.append(")");
		return queryString.toString();
	}
}
