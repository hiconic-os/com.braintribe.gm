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
public class IntegerAggregateExpert implements ArithmeticExpert<Integer> {

	public static final IntegerAggregateExpert INSTANCE = new IntegerAggregateExpert();

	private IntegerAggregateExpert() {
	}

	@Override
	public Integer add(Integer v1, Integer v2) {
		return v1 + v2;
	}

	@Override
	public Integer divide(Integer value, int count) {
		return value / count;
	}

	@Override
	public int compare(Integer v1, Integer v2) {
		return v1 - v2;
	}

}
