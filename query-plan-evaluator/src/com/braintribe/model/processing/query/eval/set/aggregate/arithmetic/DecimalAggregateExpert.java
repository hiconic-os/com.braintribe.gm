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

import java.math.BigDecimal;

import com.braintribe.model.processing.query.eval.set.aggregate.ArithmeticExpert;

/**
 * 
 */
public class DecimalAggregateExpert implements ArithmeticExpert<BigDecimal> {

	public static final DecimalAggregateExpert INSTANCE = new DecimalAggregateExpert();

	private DecimalAggregateExpert() {
	}

	@Override
	public BigDecimal add(BigDecimal v1, BigDecimal v2) {
		return v1.add(v2);
	}

	@Override
	public BigDecimal divide(BigDecimal value, int count) {
		return value.divide(new BigDecimal(count));
	}

	@Override
	public int compare(BigDecimal v1, BigDecimal v2) {
		return v1.compareTo(v2);
	}

}
