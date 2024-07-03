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
package com.braintribe.model.processing.vde.impl.bvd.logic;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Date;

import org.junit.Test;

import com.braintribe.model.bvd.logic.Disjunction;
import com.braintribe.model.bvd.logic.Negation;
import com.braintribe.model.processing.vde.evaluator.api.VdeRuntimeException;
import com.braintribe.model.processing.vde.evaluator.impl.bvd.logic.NegationVde;
import com.braintribe.model.processing.vde.impl.VDGenerator;
import com.braintribe.model.processing.vde.test.VdeTest;

/**
 * Provides tests for {@link NegationVde}.
 * 
 */
public class NegationVdeTest extends VdeTest {

	public static VDGenerator $ = new VDGenerator(); 
	
	@Test (expected= VdeRuntimeException.class)
	public void testNullOperandNegationFail() throws Exception {

		Negation logic = $.negation();

		evaluate(logic);
	}

	@Test
	public void testOperandNegation() throws Exception {

		Negation logic = $.negation();
		logic.setOperand(true);
		
		Object result = evaluate(logic);
		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(Boolean.class);
		assertThat(result).isEqualTo(false);
		
		logic.setOperand(false);
		
		result = evaluate(logic);
		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(Boolean.class);
		assertThat(result).isEqualTo(true);
		
	}


	@Test
	public void testComplexOperandNegation() throws Exception {

		Disjunction logic = Disjunction.T.create();
		Negation negation = $.negation();
		
		ArrayList<Object> operands = new ArrayList<Object>();
		operands.add(true);
		operands.add(false);
		logic.setOperands(operands);
		
		negation.setOperand(logic);
		
		Object result = evaluate(negation);
		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(Boolean.class);
		assertThat(result).isEqualTo(false);
	}
	
	@Test  (expected= VdeRuntimeException.class)
	public void testRandomOperandDisjunctionFail() throws Exception {

		Negation logic = $.negation();
		
		logic.setOperand(new Date()); // only object that evaluate to Boolean allowed
		
		evaluate(logic);
	}
}
