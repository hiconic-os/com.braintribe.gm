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
package com.braintribe.model.processing.vde.impl.bvd.time;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import com.braintribe.model.bvd.math.Subtract;
import com.braintribe.model.bvd.time.Now;
import com.braintribe.model.processing.vde.builder.api.VdBuilder;
import com.braintribe.model.processing.vde.evaluator.api.aspects.DateAspect;
import com.braintribe.model.processing.vde.evaluator.impl.bvd.time.NowVde;
import com.braintribe.model.processing.vde.impl.VDGenerator;
import com.braintribe.model.processing.vde.test.VdeTest;
import com.braintribe.model.time.TimeUnit;

/**
 * Provides tests for {@link NowVde}.
 * 
 */
public class NowVdeTest extends VdeTest  {

	public static VDGenerator $ = new VDGenerator(); 
	/**
	 * Validate that a {@link Now} will evaluate to the {@link Date} referenced in the {@link DateAspect} .
	 */
	@Test
	public void testNow() throws Exception {
		// init test data
		Now vd =  $.now();
		// Get current date
		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();

		// add date to context and evaluate the vd
		Object result =  evaluateWith(DateAspect.class, date, vd);

		// validate output
		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(Date.class);
		assertThat(result).isEqualTo(date);
		cal.set(Calendar.YEAR, 2014);
		Date otherDate = cal.getTime();
		assertThat(result).isNotEqualTo(otherDate);
	}
	
	@Test
	public void testSubstractionFromNow() {
		VdBuilder $ = VDGenerator.$;
		Subtract sub = $.subtract($.now(), $.timeSpan(1, TimeUnit.hour), $.timeSpan(3, TimeUnit.hour));
		
		Date now = new Date();
		Date threeHoursBefore = new Date(now.getTime() - 1000 * 60 * 60 * 4);
		
		Date evaluatedDate = (Date) evaluateWith(DateAspect.class, now, sub);
		
		assertThat(evaluatedDate).as("Date - TimeSpan - TimeSpan was not evaluated correctly").isEqualTo(threeHoursBefore);
	}

	
}
