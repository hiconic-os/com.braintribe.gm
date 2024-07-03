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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.braintribe.model.bvd.predicate.In;

public class InVdeTest extends AbstractPredicateVdeTest {

	
	@Test
	public void testStringIn() throws Exception {

		In predicate = $.in();
		String value = "hello";
		
		List<String> list = new ArrayList<String>();
		list.add(value);
		list.add("1");
		list.add("2");
		list.add("3");
		predicate.setLeftOperand(value);
		predicate.setRightOperand(list);

		Object result = evaluate(predicate);
		validatePositiveResult(result);

		predicate.setLeftOperand("H");

		result = evaluate(predicate);
		validateNegativeResult(result);

	}
	
	@Test
	public void testCollectionIn() throws Exception {

		In predicate = $.in();
		
		List<String> value1 = new ArrayList<String>();
		value1.add("hello");
		List<String> value2 = new ArrayList<String>();
		value1.add("hello2");
		List<String> value3 = new ArrayList<String>();
		value1.add("hello3");
		
		List<Object> list = new ArrayList<Object>();
		list.add(value1);
		list.add(value2);
		list.add(value3);
		
		predicate.setLeftOperand(value1);
		predicate.setRightOperand(list);

		Object result = evaluate(predicate);
		validatePositiveResult(result);

		predicate.setLeftOperand("H");

		result = evaluate(predicate);
		validateNegativeResult(result);

	}
}
