// ============================================================================
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
// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
// ============================================================================
package com.braintribe.model.processing.vde.impl.bvd.convert;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import com.braintribe.model.bvd.convert.ToDate;
import com.braintribe.model.processing.vde.evaluator.api.VdeRuntimeException;
import com.braintribe.model.processing.vde.evaluator.impl.bvd.convert.ToDateVde;
import com.braintribe.model.processing.vde.impl.VDGenerator;
import com.braintribe.model.processing.vde.test.VdeTest;

/**
 * Provides tests for {@link ToDateVde}.
 * 
 */
public class ToDateVdeTest extends VdeTest {

	public static VDGenerator $ = new VDGenerator(); 
	
	@Test
	public void testStringOperandStringFormatToDateConvert() throws Exception {

		ToDate convert = $.toDate();
		convert.setOperand("2001-07-04T12:08:56.235-0700");
		convert.setFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

		Object result = evaluate(convert);
		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(Date.class);
	}
	
	@Test(expected = VdeRuntimeException.class)
	public void testStringOperandStringFormatToDateConvertFormatFail() throws Exception {

		ToDate convert = $.toDate();
		convert.setOperand("2001-07-04T12:08:56.235-0700");
		convert.setFormat("."); // wrong format

		evaluate(convert);
	}

	@Test(expected = VdeRuntimeException.class)
	public void testStringOperandWrongFormatToDateConvertFormatTypeFail() throws Exception {

		ToDate convert = $.toDate();
		convert.setOperand("2001-07-04T12:08:56.235-0700");
		convert.setFormat(new Integer(3)); // wrong format

		evaluate(convert);
	}

	@Test
	public void testLongOpernadNullFormatToDateConvert() throws Exception {

		ToDate convert = $.toDate();
		convert.setOperand(Calendar.getInstance().getTime().getTime());

		Object result = evaluate(convert);
		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(Date.class);
	}

	@Test (expected = VdeRuntimeException.class)
	public void testLongOperandRandomFormatToDateConvertFormatFail() throws Exception {

		ToDate convert = $.toDate();
		convert.setOperand(Calendar.getInstance().getTime().getTime());
		convert.setFormat(" ");// format not applicable with null

		evaluate(convert);
	}

	@Test (expected = VdeRuntimeException.class)
	public void testRandomOperandNullFormatToDateConvertTypeFail() throws Exception {
		
		ToDate convert = $.toDate();
		convert.setOperand(new Date()); // only string, long allowed
		evaluate(convert);
	}
}
