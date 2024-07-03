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
package com.braintribe.model.processing.query.stringifier.experts.basic;

import com.braintribe.model.processing.query.api.stringifier.QueryStringifierRuntimeException;
import com.braintribe.model.processing.query.api.stringifier.experts.Stringifier;
import com.braintribe.model.processing.query.api.stringifier.experts.StringifierContext;

public class StringStringifier implements Stringifier<String, StringifierContext> {
	@Override
	public String stringify(String operand, StringifierContext context) throws QueryStringifierRuntimeException {
		StringBuilder queryString = new StringBuilder();
		queryString.append("'");

		// Prepare string operand by escaping
		for (final char character : operand.toCharArray()) {
			switch (character) {
			case '\\': {
				queryString.append("\\");
				break;
			}
			case '\'': {
				queryString.append("\'");
				break;
			}
			case '\b': {
				queryString.append("\b");
				break;
			}
			case '\t': {
				queryString.append("\t");
				break;
			}
			case '\n': {
				queryString.append("\n");
				break;
			}
			case '\f': {
				queryString.append("\f");
				break;
			}
			case '\r': {
				queryString.append("\r");
				break;
			}
			default: {
				queryString.append(character);
				break;
			}
			}
		}

		queryString.append("'");
		return queryString.toString();
	}
}
