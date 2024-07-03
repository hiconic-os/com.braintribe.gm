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
package com.braintribe.model.processing.vde.impl.bvd.convert;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

import org.junit.Test;

import com.braintribe.model.bvd.convert.ToLong;
import com.braintribe.model.processing.vde.evaluator.api.VdeRuntimeException;
import com.braintribe.model.processing.vde.evaluator.impl.bvd.convert.ToLongVde;
import com.braintribe.model.processing.vde.impl.VDGenerator;
import com.braintribe.model.processing.vde.impl.misc.Name;
import com.braintribe.model.processing.vde.test.VdeTest;

/**
 * Provides tests for {@link ToLongVde}.
 * 
 */
public class ToLongVdeTest extends VdeTest {

	public static VDGenerator $ = new VDGenerator(); 
	
	@Test
	public void testStringOperandNullFormatToLongConvert() throws Exception {

		ToLong convert = $.toLong();
		convert.setOperand("4");

		Object result = evaluate(convert);
		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(Long.class);
		assertThat(result).isEqualTo(new Long(4));
	}
	
	@Test  (expected = VdeRuntimeException.class)
	public void testStringOperandRandomFormatToLongConvertFormatFail() throws Exception {

		ToLong convert = $.toLong();
		convert.setOperand("4");
		convert.setFormat(" "); // formats not taken into consideration till now

		evaluate(convert);
	}	
	
	@Test
	public void testBooleanOperandNullFormatToLongConvert() throws Exception {

		ToLong convert = $.toLong();
		convert.setOperand(true);

		Object result = evaluate(convert);
		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(Long.class);
		assertThat(result).isEqualTo(new Long(1));
		
		convert.setOperand(false);

		result = evaluate(convert);
		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(Long.class);
		assertThat(result).isEqualTo(new Long(0));
	}
	

	@Test
	public void testDateOperandNullFormatToLongConvertTypeFail() throws Exception {
		
		ToLong convert = $.toLong();
		convert.setOperand(new Date()); 
		Object result = evaluate(convert);
		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(Long.class);
	}
	

	@Test (expected = VdeRuntimeException.class)
	public void testRandomOperandNullFormatToLongConvertTypeFail() throws Exception {
		
		ToLong convert = $.toLong();
		convert.setOperand(Name.T.create()); // only string, boolean, date allowed
		evaluate(convert);
	}
	
}
