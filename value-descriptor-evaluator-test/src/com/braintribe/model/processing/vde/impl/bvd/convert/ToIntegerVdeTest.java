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

import com.braintribe.model.bvd.convert.ToInteger;
import com.braintribe.model.processing.vde.evaluator.api.VdeRuntimeException;
import com.braintribe.model.processing.vde.evaluator.impl.bvd.convert.ToIntegerVde;
import com.braintribe.model.processing.vde.impl.VDGenerator;
import com.braintribe.model.processing.vde.impl.misc.SalaryRange;
import com.braintribe.model.processing.vde.test.VdeTest;

/**
 * Provides tests for {@link ToIntegerVde}.
 * 
 */
public class ToIntegerVdeTest extends VdeTest {

	public static VDGenerator $ = new VDGenerator(); 
	
	@Test
	public void testStringOperandNullFormatToIntegerConvert() throws Exception {

		ToInteger convert = $.toInteger();
		convert.setOperand("4");

		Object result = evaluate(convert);
		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(Integer.class);
		assertThat(result).isEqualTo(new Integer(4));
	}

	@Test(expected = VdeRuntimeException.class)
	public void testStringOperandRandomFormatToIntegerConvertFormatFail() throws Exception {

		ToInteger convert = $.toInteger();
		convert.setOperand("4");
		convert.setFormat(" "); // formats not taken into consideration till now

		evaluate(convert);
	}

	@Test
	public void testBooleanOperandNullFormatToIntegerConvert() throws Exception {

		ToInteger convert = $.toInteger();
		convert.setOperand(true);

		Object result = evaluate(convert);
		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(Integer.class);
		assertThat(result).isEqualTo(new Integer(1));

		convert.setOperand(false);

		result = evaluate(convert);
		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(Integer.class);
		assertThat(result).isEqualTo(new Integer(0));
	}

	@Test
	public void testEnumOperandNullFormatToIntegerConvert() throws Exception {

		ToInteger convert = $.toInteger();
		convert.setOperand(SalaryRange.low);

		Object result = evaluate(convert);
		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(Integer.class);
		assertThat(result).isEqualTo(new Integer(0));

	}

	@Test(expected = VdeRuntimeException.class)
	public void testRandomOperandNullFormatToIntegerConvertTypeFail() throws Exception {

		ToInteger convert = $.toInteger();
		convert.setOperand(new Date()); // only string, boolean allowed
		evaluate(convert);
	}

}
