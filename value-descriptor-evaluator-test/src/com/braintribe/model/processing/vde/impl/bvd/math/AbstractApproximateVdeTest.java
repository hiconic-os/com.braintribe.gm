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

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import com.braintribe.model.bvd.math.ApproximateOperation;
import com.braintribe.model.processing.vde.impl.VDGenerator;
import com.braintribe.model.processing.vde.test.VdeTest;

public abstract class AbstractApproximateVdeTest extends VdeTest {

	public static VDGenerator $ = new VDGenerator(); 
	
	private void validateResult(Object result, Object expectedResult, Class<?> clazz){
		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(clazz);
		assertThat(result).isEqualTo(expectedResult);
	}
	
	protected void validateIntegerResult(Object result, Object expectedResult) {
		validateResult(result, expectedResult, Integer.class);
	}

	protected void validateLongResult(Object result, Object expectedResult) {
		validateResult(result, expectedResult, Long.class);
	}

	protected void validateFloatResult(Object result, Object expectedResult) {
		validateResult(result, expectedResult, Float.class);
	}

	protected void validateDoubleResult(Object result, Object expectedResult) {
		validateResult(result, expectedResult, Double.class);
	}

	protected void validateDecimalResult(Object result, Object expectedResult) {
		validateResult(result, expectedResult, BigDecimal.class);
	}

	protected void testNullEmptyOperands(ApproximateOperation operation) throws Exception {
		evaluate(operation);
	}
	
	
	protected Date getSampleDate(){
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 2011);
		cal.set(Calendar.MONTH, 3);
		cal.set(Calendar.DAY_OF_MONTH, 7);
		cal.set(Calendar.HOUR_OF_DAY, 15);
		cal.set(Calendar.MINUTE, 33);
		cal.set(Calendar.SECOND, 11);
		cal.set(Calendar.MILLISECOND, 39);
		
		return cal.getTime();
	}
	
	private void validateDate(Calendar result, Calendar expected){
		validateCalendarEntry(Calendar.YEAR, result, expected);
		validateCalendarEntry(Calendar.MONTH, result, expected);
		validateCalendarEntry(Calendar.DAY_OF_MONTH, result, expected);
		validateCalendarEntry(Calendar.HOUR_OF_DAY, result, expected);
		validateCalendarEntry(Calendar.MINUTE, result, expected);
		validateCalendarEntry(Calendar.SECOND, result, expected);
		validateCalendarEntry(Calendar.MILLISECOND, result, expected);
	}
	private void validateCalendarEntry(int field,Calendar result, Calendar expected){
		assertThat(result.get(field)).isEqualTo(expected.get(field));
	}
	
	protected void validateDate(Object result, Date expected){
		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(Date.class);
		validateDate((Date)result, expected);
	}
	
	private void validateDate(Date result, Date expected){
		Calendar calResult = Calendar.getInstance();
		calResult.setTime(result);
		Calendar calExpected = Calendar.getInstance();
		calExpected.setTime(expected);
		validateDate(calResult, calExpected);
	}
}
