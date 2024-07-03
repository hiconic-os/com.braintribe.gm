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
package com.braintribe.model.processing.vde.impl.bvd.conditional;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.braintribe.model.bvd.conditional.Coalesce;
import com.braintribe.model.processing.vde.evaluator.impl.bvd.convert.ToStringVde;
import com.braintribe.model.processing.vde.impl.VDGenerator;
import com.braintribe.model.processing.vde.test.VdeTest;

/**
 * Provides tests for {@link ToStringVde}.
 * 
 */
public class CoalesceVdeTest extends VdeTest {

	@Test
	public void testStringNullCoalesce() throws Exception {
		String operand = null;
		String replacement = "foo";
		
		Coalesce coalesce = VDGenerator.$.coalesce(operand, replacement);

		Object result = evaluate(coalesce);
		assertThat(result).isNotNull();
		assertThat(result).isEqualTo(replacement);
	}


}
