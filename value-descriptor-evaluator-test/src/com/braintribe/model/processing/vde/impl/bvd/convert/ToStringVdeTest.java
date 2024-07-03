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

import static com.braintribe.testing.junit.assertions.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.junit.Test;

import com.braintribe.model.bvd.convert.ToString;
import com.braintribe.model.processing.vde.evaluator.api.VdeRuntimeException;
import com.braintribe.model.processing.vde.evaluator.impl.bvd.convert.ToStringVde;
import com.braintribe.model.processing.vde.impl.VDGenerator;
import com.braintribe.model.processing.vde.impl.misc.Person;
import com.braintribe.model.processing.vde.test.VdeTest;

/**
 * Provides tests for {@link ToStringVde}.
 * 
 */
public class ToStringVdeTest extends VdeTest {

	public static VDGenerator $ = new VDGenerator();

	@Test
	public void testDateOperandStringFormatToDateConvert() throws Exception {

		ToString convert = $.ToString();
		Date date = Calendar.getInstance().getTime();
		convert.setOperand(date);
		convert.setFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"); // valid format

		Object result = evaluate(convert);
		validateStringResult(result);
	}

	@Test(expected = VdeRuntimeException.class)
	public void testDateOperandStringFormatToDateConvertFormatFail() throws Exception {

		ToString convert = $.ToString();
		Date date = Calendar.getInstance().getTime();
		convert.setOperand(date);
		convert.setFormat("wrong"); // wrong format

		evaluate(convert);
	}

	@Test(expected = VdeRuntimeException.class)
	public void testDateOperandStringFormatToDateConvertFormatTypeFail() throws Exception {

		ToString convert = $.ToString();
		Date date = Calendar.getInstance().getTime();
		convert.setOperand(date);
		convert.setFormat(new Integer(4)); // wrong format type

		evaluate(convert);
	}

	@Test
	public void testDateOperandNullFormatToDateConvert() throws Exception {

		ToString convert = $.ToString();
		Date date = Calendar.getInstance().getTime();
		convert.setOperand(date);

		Object result = evaluate(convert);
		validateStringResult(result);
	}

	@Test
	public void testRandomOperandNullFormatToDateConvert() throws Exception {

		ToString convert = $.ToString();
		convert.setOperand(Person.T.create());

		Object result = evaluate(convert);
		validateStringResult(result);

	}

	@Test
	public void testNumberOperandNullFormatToDateConvert() throws Exception {

		ToString convert = $.ToString();
		convert.setOperand(new Integer(3));

		Object result = evaluate(convert);
		validateStringResult(result);

		convert.setOperand(new BigDecimal(2));

		result = evaluate(convert);
		validateStringResult(result);
	}

	@Test(expected = VdeRuntimeException.class)
	public void testNumberOperandRandomFormatToDateConvert() throws Exception {
		ToString convert = $.ToString();
		convert.setOperand(new Integer(3));
		convert.setFormat(new Date());

		evaluate(convert);
	}

	@Test
	public void testNumberOperandNumberFormatToDateConvert() throws Exception {
		Locale.setDefault(Locale.ENGLISH);

		ToString convert = $.ToString();
		String format = "###.##";
		convert.setOperand(new Integer(3));
		convert.setFormat(format);

		Object result = evaluate(convert);
		validateStringResult(result, "3");

		convert.setOperand(new BigDecimal(2));

		result = evaluate(convert);
		validateStringResult(result, "2");

		convert.setOperand(new Double(2.423));

		result = evaluate(convert);
		validateStringResult(result, "2.42");
	}

	private void validateStringResult(Object result) {
		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(String.class);
	}

	private void validateStringResult(Object result, String expectedResult) {
		validateStringResult(result);
		assertThat((String) result).isEqualTo(expectedResult);
	}
}
