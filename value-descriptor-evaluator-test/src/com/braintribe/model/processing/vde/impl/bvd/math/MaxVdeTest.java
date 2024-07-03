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
package com.braintribe.model.processing.vde.impl.bvd.math;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.braintribe.model.bvd.math.Max;
import com.braintribe.model.processing.vde.evaluator.api.VdeRuntimeException;
import com.braintribe.model.processing.vde.evaluator.impl.bvd.math.MaxVde;

/**
 * Provides tests for {@link MaxVde}.
 * 
 */
public class MaxVdeTest extends AbstractArithmeticVdeTest {

	@Test(expected = VdeRuntimeException.class)
	public void testNullOperandsMax() throws Exception {

		Max math = $.max();

		testNullEmptyOperands(math);
	}

	@Test(expected = VdeRuntimeException.class)
	public void testEmptyOperandsMax() throws Exception {

		Max math = $.max();
		List<Object> operands = new ArrayList<Object>();
		math.setOperands(operands);

		testNullEmptyOperands(math);
	}

	@Test
	public void testSingleOperandMax() throws Exception {

		Max math = $.max();
		List<Object> operands = new ArrayList<Object>();
		operands.add(new Integer(3));
		math.setOperands(operands);

		testSingleOperand(math);
	}

	@Test
	public void testIntegerMax() throws Exception {

		Max math = $.max();
		List<Object> operands = new ArrayList<Object>();
		operands.add(new Integer(2));
		operands.add(new Integer(3));
		operands.add(new Integer(1));
		math.setOperands(operands);

		Object result = evaluate(math);
		validateIntegerResult(result, new Integer(3));
	}

	@Test
	public void testLongMax() throws Exception {

		Max math = $.max();
		List<List<Object>> operandsMax = getLongOperandsMax();

		for (List<Object> operandList : operandsMax) {

			math.setOperands(operandList);
			Object result = evaluate(math);
			validateLongResult(result, new Long(4));
		}

		operandsMax = getLongOperandsMax();
		Collections.reverse(operandsMax);

		for (List<Object> operandList : operandsMax) {

			math.setOperands(operandList);
			Object result = evaluate(math);
			validateLongResult(result, new Long(4));
		}
	}

	@Test
	public void testFloatMax() throws Exception {

		Max math = $.max();
		List<List<Object>> operandsMax = getFloatOperandsMax();

		for (List<Object> operandList : operandsMax) {
			math.setOperands(operandList);
			Object result = evaluate(math);
			validateFloatResult(result,new Float(4));
		}

		operandsMax = getFloatOperandsMax();
		Collections.reverse(operandsMax);

		for (List<Object> operandList : operandsMax) {

			math.setOperands(operandList);
			Object result = evaluate(math);
			validateFloatResult(result,new Float(4));
		}
	}

	@Test
	public void testDoubleMax() throws Exception {

		Max math = $.max();
		List<List<Object>> operandsMax = getDoubleOperandsMax();

		for (List<Object> operandList : operandsMax) {
			math.setOperands(operandList);
			Object result = evaluate(math);
			validateDoubleResult(result,new Double(4));
		}

		operandsMax = getDoubleOperandsMax();
		Collections.reverse(operandsMax);

		for (List<Object> operandList : operandsMax) {

			math.setOperands(operandList);
			Object result = evaluate(math);
			validateDoubleResult(result,new Double(4));
		}
	}

	@Test
	public void testDecimalMax() throws Exception {
		Max math = $.max();
		List<List<Object>> operandsMax = getDecimalOperandsMax();

		for (List<Object> operandList : operandsMax) {
			math.setOperands(operandList);
			Object result = evaluate(math);
			validateDecimalResult(result, new BigDecimal(4));
		}

		operandsMax = getDecimalOperandsMax();
		Collections.reverse(operandsMax);

		for (List<Object> operandList : operandsMax) {

			math.setOperands(operandList);
			Object result = evaluate(math);
			validateDecimalResult(result, new BigDecimal(4));
		}
	}

	private List<List<Object>> getLongOperandsMax() {
		List<List<Object>> result = new ArrayList<List<Object>>();

		List<Object> list = new ArrayList<Object>();
		list.add(new Long(2));
		list.add(new Long(3));
		list.add(new Long(4));
		result.add(list);

		list = new ArrayList<Object>();
		list.add(new Long(2));
		list.add(new Integer(3));
		list.add(new Long(4));
		result.add(list);

		return result;
	}

	private List<List<Object>> getFloatOperandsMax() {
		List<List<Object>> result = new ArrayList<List<Object>>();

		List<Object> list = new ArrayList<Object>();
		list.add(new Float(2));
		list.add(new Float(3));
		list.add(new Float(4));
		result.add(list);

		list = new ArrayList<Object>();
		list.add(new Float(2));
		list.add(new Integer(3));
		list.add(new Float(4));
		result.add(list);

		list = new ArrayList<Object>();
		list.add(new Float(2));
		list.add(new Long(3));
		list.add(new Float(4));
		result.add(list);

		return result;
	}

	private List<List<Object>> getDoubleOperandsMax() {
		List<List<Object>> result = new ArrayList<List<Object>>();

		List<Object> list = new ArrayList<Object>();
		list.add(new Double(2));
		list.add(new Double(3));
		list.add(new Double(4));
		result.add(list);

		list = new ArrayList<Object>();
		list.add(new Double(2));
		list.add(new Integer(3));
		list.add(new Double(4));
		result.add(list);

		list = new ArrayList<Object>();
		list.add(new Double(2));
		list.add(new Long(3));
		list.add(new Double(4));
		result.add(list);

		list = new ArrayList<Object>();
		list.add(new Double(2));
		list.add(new Float(3));
		list.add(new Double(4));
		result.add(list);

		return result;
	}

	private List<List<Object>> getDecimalOperandsMax() {
		List<List<Object>> result = new ArrayList<List<Object>>();

		List<Object> list = new ArrayList<Object>();
		list.add(new BigDecimal(2));
		list.add(new BigDecimal(3));
		list.add(new BigDecimal(4));
		result.add(list);

		list = new ArrayList<Object>();
		list.add(new BigDecimal(2));
		list.add(new Integer(3));
		list.add(new BigDecimal(4));
		result.add(list);

		list = new ArrayList<Object>();
		list.add(new BigDecimal(2));
		list.add(new Long(3));
		list.add(new BigDecimal(4));
		result.add(list);

		list = new ArrayList<Object>();
		list.add(new BigDecimal(2));
		list.add(new Float(3));
		list.add(new BigDecimal(4));
		result.add(list);

		list = new ArrayList<Object>();
		list.add(new BigDecimal(2));
		list.add(new Double(3));
		list.add(new BigDecimal(4));
		result.add(list);

		return result;
	}
}
