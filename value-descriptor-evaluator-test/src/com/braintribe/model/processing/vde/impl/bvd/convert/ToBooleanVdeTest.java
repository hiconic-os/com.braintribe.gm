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

import org.junit.Test;

import com.braintribe.model.bvd.convert.ToBoolean;
import com.braintribe.model.processing.vde.evaluator.api.VdeRuntimeException;
import com.braintribe.model.processing.vde.evaluator.impl.bvd.convert.ToBooleanVde;
import com.braintribe.model.processing.vde.impl.VDGenerator;
import com.braintribe.model.processing.vde.test.VdeTest;

/**
 * Provides tests for {@link ToBooleanVde}.
 * 
 */
public class ToBooleanVdeTest extends VdeTest {
	
	public static VDGenerator $ = new VDGenerator(); 

	@Test
	public void testStringOperandNullFomratToBooleanTrueConvert() throws Exception {

		ToBoolean convert = $.toBoolean();

		convert.setOperand("true"); // TRUE works too
		Object result = evaluate(convert);
		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(Boolean.class);
		assertThat(result).isEqualTo(true);
	}

	@Test
	public void testStringOperandNullFomratToBooleanFalseConvert() throws Exception {

		ToBoolean convert = $.toBoolean();

		convert.setOperand("false");
		Object result = evaluate(convert);

		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(Boolean.class);
		assertThat(result).isEqualTo(false);

		convert.setOperand("xyz");
		result = evaluate(convert);

		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(Boolean.class);
		assertThat(result).isEqualTo(false);
	}

	@Test(expected = VdeRuntimeException.class)
	public void testStringOperandRandomFomratToBooleanConvertFormatFail() throws Exception {

		ToBoolean convert = $.toBoolean();

		convert.setOperand("true");
		convert.setFormat("format"); // format is not implemented for ToBoolean
		evaluate(convert);
	}

	@Test(expected = VdeRuntimeException.class)
	public void testRandomOperandNullFomratToBooleanConvertOperandFail() throws Exception {
		ToBoolean convert = $.toBoolean();

		convert.setOperand(new Integer(30)); // only string allowed
		evaluate(convert);
	}
}
