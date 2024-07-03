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

import java.util.Date;

import com.braintribe.model.processing.query.eval.set.aggregate.ArithmeticExpert;

/**
 * 
 */
public class DateAggregateExpert implements ArithmeticExpert<Date> {

	public static final DateAggregateExpert INSTANCE = new DateAggregateExpert();

	private DateAggregateExpert() {
	}

	@Override
	public Date add(Date v1, Date v2) {
		return new Date(v1.getTime() + v2.getTime());
	}

	@Override
	public Date divide(Date value, int count) {
		return new Date(value.getTime() / count);
	}

	@Override
	public int compare(Date v1, Date v2) {
		return v1.compareTo(v2);
	}

}
