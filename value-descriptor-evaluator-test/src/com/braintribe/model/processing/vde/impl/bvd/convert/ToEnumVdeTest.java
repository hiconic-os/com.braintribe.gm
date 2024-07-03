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

import com.braintribe.model.bvd.convert.ToEnum;
import com.braintribe.model.generic.reflection.GenericModelException;
import com.braintribe.model.processing.vde.evaluator.api.VdeRuntimeException;
import com.braintribe.model.processing.vde.evaluator.impl.bvd.convert.ToEnumVde;
import com.braintribe.model.processing.vde.impl.VDGenerator;
import com.braintribe.model.processing.vde.impl.misc.SalaryRange;
import com.braintribe.model.processing.vde.test.VdeTest;

/**
 * Provides tests for {@link ToEnumVde}.
 * 
 */
public class ToEnumVdeTest extends VdeTest {

	public static VDGenerator $ = new VDGenerator(); 
	
	@Test
	public void testValidTypeStringOperandToEnumConvert() throws Exception {

		ToEnum convert = $.toEnum();
		convert.setTypeSignature(SalaryRange.class.getName());
		convert.setOperand("low");

		Object result = evaluate(convert);
		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(Enum.class);
		assertThat(result).isEqualTo(SalaryRange.low);
	}

	@Test
	public void testValidTypeIntegerOperandToEnumConvert() throws Exception {

		ToEnum convert = $.toEnum();
		convert.setTypeSignature(SalaryRange.class.getName());
		convert.setOperand(0);

		Object result = evaluate(convert);
		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(Enum.class);
		assertThat(result).isEqualTo(SalaryRange.low);
	}

	@Test(expected = VdeRuntimeException.class)
	public void testValidTypeRandomOperandToEnumConvertOperandFail() throws Exception {

		ToEnum convert = $.toEnum();
		convert.setTypeSignature(SalaryRange.class.getName());
		convert.setOperand(new Date()); // only string or int are allowed

		evaluate(convert);
	}
	
	@Test (expected = GenericModelException.class)
	public void testRandomTypeNullOperandToEnumConvertTypeSignatureFail() throws Exception {

		ToEnum convert = $.toEnum();
		convert.setTypeSignature("random");

		evaluate(convert);
	}	
}
