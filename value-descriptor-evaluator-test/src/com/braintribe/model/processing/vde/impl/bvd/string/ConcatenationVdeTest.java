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
package com.braintribe.model.processing.vde.impl.bvd.string;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.braintribe.model.bvd.string.Concatenation;
import com.braintribe.model.bvd.string.Lower;
import com.braintribe.model.processing.vde.evaluator.api.VdeRuntimeException;

public class ConcatenationVdeTest extends AbstractStringVdeTest {

	@Test
	public void testMultipleStringConcatenation() throws Exception {

		Concatenation stringFunction = $.concatenation();
		List<Object> list = new ArrayList<Object>();
		list.add("hello");
		list.add("1");
		list.add("2");
		list.add("3");
		list.add("4");
		stringFunction.setOperands(list);

		Object result = evaluate(stringFunction);
		validateResult(result, "hello1234");

	}

	@Test
	public void testSingleStringConcatenation() throws Exception {

		Concatenation stringFunction = $.concatenation();
		List<Object> list = new ArrayList<Object>();
		list.add("hello");
		stringFunction.setOperands(list);

		Object result = evaluate(stringFunction);
		validateResult(result, "hello");

	}

	@Test
	public void testNullOperandsConcatenation() throws Exception {

		Concatenation stringFunction = $.concatenation();

		Object result = evaluate(stringFunction);
		validateResult(result, "");

	}

	@Test(expected = VdeRuntimeException.class)
	public void testWrongOperandsConcatenationFail() throws Exception {

		Concatenation stringFunction = $.concatenation();
		List<Object> list = new ArrayList<Object>();
		list.add("hello");
		list.add(new Date()); // not string
		stringFunction.setOperands(list);

		evaluate(stringFunction);

	}
	
	@Test
	public void testMultipleTypeStringConcatenation() throws Exception {

		Concatenation stringFunction = $.concatenation();
		List<Object> list = new ArrayList<Object>();
		Lower lower = Lower.T.create();
		lower.setOperand("HeLLo");
		list.add(lower);
		list.add("1");
		list.add("2");
		list.add("3");
		list.add("4");
		stringFunction.setOperands(list);

		Object result = evaluate(stringFunction);
		validateResult(result, "hello1234");

	}
	
}
