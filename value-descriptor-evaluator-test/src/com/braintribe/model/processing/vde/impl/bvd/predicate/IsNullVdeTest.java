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
package com.braintribe.model.processing.vde.impl.bvd.predicate;

import org.junit.Test;

import com.braintribe.model.bvd.predicate.IsNull;

public class IsNullVdeTest extends AbstractPredicateVdeTest {

	@Test
	public void testOperandEqualNull() throws Exception {

		Object operand = null;

		IsNull predicate = IsNull.T.create();
		predicate.setOperand(operand);

		Object result = evaluate(predicate);
		validatePositiveResult(result);

	}

	@Test
	public void testOperandNotEqualNull() throws Exception {

		Object operand = "foo";

		IsNull predicate = IsNull.T.create();
		predicate.setOperand(operand);

		Object result = evaluate(predicate);
		validateNegativeResult(result);

	}

}
