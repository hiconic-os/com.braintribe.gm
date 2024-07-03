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
package com.braintribe.model.processing.query.parser.impl.context;

import com.braintribe.model.processing.query.parser.impl.listener.GmqlBasicParserListener;

/**
 * A wrapper object that holds partial or full results during parsing.
 * 
 * During query parsing, maintaining a parse tree would really be expensive.
 * Thus the solution, is to keep only the needed information from the parsing,
 * i.e. the partial/full results of the rules.
 * 
 * This construct is used extensively in {@link GmqlBasicParserListener}.
 * 
 * @param <R>
 *            Type of object that is wrapped
 */
public class CustomContext<R> {

	private R returnValue;

	public CustomContext(R returnValue) {
		this.returnValue = returnValue;
	}

	@SuppressWarnings("unchecked")
	public <C extends CustomContext<?>> C cast() {
		return (C) ((CustomContext<?>) this);
	}

	public R getReturnValue() {
		return returnValue;
	}

}
