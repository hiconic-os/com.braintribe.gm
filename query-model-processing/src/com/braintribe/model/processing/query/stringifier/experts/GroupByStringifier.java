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
import com.braintribe.model.query.GroupBy;

public class GroupByStringifier implements Stringifier<GroupBy, BasicQueryStringifierContext> {
	@Override
	public String stringify(GroupBy groupBy, BasicQueryStringifierContext context) throws QueryStringifierRuntimeException {
		StringBuilder queryString = new StringBuilder();

		List<Object> operands = groupBy.getOperands();
		if (operands != null && operands.size() > 0) {
			// Work through all operands
			for (int i = 0, l = operands.size(); i < l; i++) {
				if (i > 0) {
					// Add operand splitter
					queryString.append(", ");
				}

				// Add operand to queryString
				context.stringifyAndAppend(operands.get(i), queryString);
			}
		}

		return queryString.toString();
	}
}
