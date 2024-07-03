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

import static com.braintribe.utils.lcd.CollectionTools2.newSet;

import java.util.Set;

import com.braintribe.model.processing.query.eval.set.aggregate.ArithmeticExpert;

/**
 * 
 */
public class CountDistinctAggregateExpert implements ArithmeticExpert<Object> {

	private final Set<Object> visited = newSet();

	@Override
	public Object add(Object v1, Object v2) {
		if (v2 == null || visited.contains(v2))
			return v1;

		visited.add(v2);

		return (Long) v1 + 1;
	}

	@Override
	public Object divide(Object value, int count) {
		throw new UnsupportedOperationException("Cannot divide Object by int. This is an expert for count distinct!");
	}

	@Override
	public int compare(Object v1, Object v2) {
		throw new UnsupportedOperationException("Comparing not expected. This is an expert for count distinct!");
	}

}
