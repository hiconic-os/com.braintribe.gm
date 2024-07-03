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
package com.braintribe.model.processing.query.eval.set.aggregate.arithmetic;

import com.braintribe.model.processing.query.eval.set.aggregate.ArithmeticExpert;

/**
 * 
 */
public class StringAggregateExpert implements ArithmeticExpert<String> {

	public static final StringAggregateExpert INSTANCE = new StringAggregateExpert();

	private StringAggregateExpert() {
	}

	@Override
	public String add(String v1, String v2) {
		throw new UnsupportedOperationException("Cannot add two strings.");
	}

	@Override
	public String divide(String value, int count) {
		throw new UnsupportedOperationException("Cannot divide string by int.");
	}

	@Override
	public int compare(String v1, String v2) {
		return v1.compareTo(v2);
	}

}
