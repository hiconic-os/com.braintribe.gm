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

import com.braintribe.model.bvd.logic.Conjunction;
import com.braintribe.model.bvd.logic.Negation;
import com.braintribe.model.processing.vde.evaluator.api.VdeRuntimeException;
import com.braintribe.model.processing.vde.evaluator.impl.bvd.logic.ConjunctionVde;
import com.braintribe.model.processing.vde.impl.VDGenerator;
import com.braintribe.model.processing.vde.test.VdeTest;

/**
 * Provides tests for {@link ConjunctionVde}.
 * 
 */
public class ConjunctionVdeTest extends VdeTest {

	public static VDGenerator $ = new VDGenerator(); 
	
	@Test
	public void testNullOperandConjunctionFail() throws Exception {

		Conjunction logic = $.conjunction();
		
		Object result = evaluate(logic);
		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(Boolean.class);
		assertThat(result).isEqualTo(true);
	}

	@Test
	public void testEmptyOperandConjunction() throws Exception {

		Conjunction logic = $.conjunction();
		logic.setOperands(new ArrayList<Object>());
		
		Object result = evaluate(logic);
		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(Boolean.class);
		assertThat(result).isEqualTo(true);
	}

	@Test
	public void testMultipleSimpleOperandConjunction() throws Exception {

		Conjunction logic = $.conjunction();
		ArrayList<Object> operands = new ArrayList<Object>();
		operands.add(true);
		operands.add(false);
		operands.add(true);
		logic.setOperands(operands);
		
		Object result = evaluate(logic);
		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(Boolean.class);
		assertThat(result).isEqualTo(false);
	}

	@Test
	public void testMultipleOperandConjunction() throws Exception {

		Conjunction logic = $.conjunction();
		Negation negation = Negation.T.create();
		negation.setOperand(false);
		
		ArrayList<Object> operands = new ArrayList<Object>();
		operands.add(true);
		operands.add(negation);
		operands.add(true);
		logic.setOperands(operands);
		
		Object result = evaluate(logic);
		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(Boolean.class);
		assertThat(result).isEqualTo(true);
	}
	
	@Test  (expected= VdeRuntimeException.class)
	public void testMultipleOperandConjunctionFail() throws Exception {

		Conjunction logic = $.conjunction();
		
		ArrayList<Object> operands = new ArrayList<Object>();
		operands.add(true);
		operands.add(new Date()); // only object that evaluate to Boolean allowed
		operands.add(true);
		logic.setOperands(operands);
		
		evaluate(logic);
	}
}
