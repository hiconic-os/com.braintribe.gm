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
import com.braintribe.model.processing.vde.evaluator.impl.bvd.logic.DisjunctionVde;
import com.braintribe.model.processing.vde.impl.VDGenerator;
import com.braintribe.model.processing.vde.test.VdeTest;

/**
 * Provides tests for {@link DisjunctionVde}.
 * 
 */
public class DisjunctionVdeTest extends VdeTest {

	public static VDGenerator $ = new VDGenerator(); 
	
	@Test
	public void testNullOperandDisjunction() throws Exception {

		Disjunction logic = $.disjunction();

		Object result = evaluate(logic);
		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(Boolean.class);
		assertThat(result).isEqualTo(false);
	}

	@Test
	public void testEmptyOperandDisjunction() throws Exception {

		Disjunction logic = $.disjunction();
		logic.setOperands(new ArrayList<Object>());
		
		Object result = evaluate(logic);
		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(Boolean.class);
		assertThat(result).isEqualTo(false);
	}

	@Test
	public void testMultipleSimpleOperandDisjunction() throws Exception {

		Disjunction logic = $.disjunction();
		ArrayList<Object> operands = new ArrayList<Object>();
		operands.add(true);
		operands.add(false);
		operands.add(true);
		logic.setOperands(operands);
		
		Object result = evaluate(logic);
		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(Boolean.class);
		assertThat(result).isEqualTo(true);
	}

	@Test
	public void testMultipleOperandDisjunction() throws Exception {

		Disjunction logic = $.disjunction();
		Negation negation = Negation.T.create();
		negation.setOperand(true);
		
		ArrayList<Object> operands = new ArrayList<Object>();
		operands.add(false);
		operands.add(negation);
		operands.add(false);
		logic.setOperands(operands);
		
		Object result = evaluate(logic);
		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(Boolean.class);
		assertThat(result).isEqualTo(false);
	}
	
	@Test  (expected= VdeRuntimeException.class)
	public void testMultipleOperandDisjunctionFail() throws Exception {

		Disjunction logic = $.disjunction();
		
		ArrayList<Object> operands = new ArrayList<Object>();
		operands.add(true);
		operands.add(new Date()); // only object that evaluate to Boolean allowed
		operands.add(true);
		logic.setOperands(operands);
		
		evaluate(logic);
	}
}
