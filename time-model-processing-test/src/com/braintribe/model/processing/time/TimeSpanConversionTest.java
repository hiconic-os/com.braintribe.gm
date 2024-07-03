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
package com.braintribe.model.processing.time;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.braintribe.model.time.TimeSpan;
import com.braintribe.model.time.TimeUnit;

public class TimeSpanConversionTest {

	private double TEST_DOUBLE = 98327598345.2234345;
	private long TEST_LONG = Double.valueOf(TEST_DOUBLE).longValue();

	@Test
	public void testGetDurationSecond() throws Exception {
		TimeSpan timeSpan = createTimeSpan(1, TimeUnit.second);
		Duration duration = TimeSpanConversion.getDuration(timeSpan);
		long seconds = duration.getSeconds();
		assertThat(seconds).isEqualTo(1);
	}

	@Test
	public void testGetDurationMicroSecond() throws Exception {
		TimeSpan timeSpan = createTimeSpan(1, TimeUnit.microSecond);
		Duration duration = TimeSpanConversion.getDuration(timeSpan);
		long seconds = duration.getNano();
		assertThat(seconds).isEqualTo(1000);
	}

	@Test
	public void testGetDurationManySecond() throws Exception {
		TimeSpan timeSpan = createTimeSpan(TEST_DOUBLE, TimeUnit.second);
		Duration duration = TimeSpanConversion.getDuration(timeSpan);
		long seconds = duration.getSeconds();
		assertThat(seconds).isEqualTo(TEST_LONG);
	}
	
	@Test
	public void testAnalysis() throws Exception {
		TimeSpan spans[] = {
				createTimeSpan(1.554, TimeUnit.hour),
				createTimeSpan(2000, TimeUnit.milliSecond),
				createTimeSpan(3.5, TimeUnit.minute),
				createTimeSpan(25.545235243435243524352435, TimeUnit.hour),
				createTimeSpan(1 + 1.0/60/60 * 3, TimeUnit.hour),
				createTimeSpan(1 + 1.0/60/1000 * 3, TimeUnit.minute)
		};
		
		List<List<TimeSpan>> expectedPartLists = Arrays.asList(
				Arrays.asList(createTimeSpan(1, TimeUnit.hour), createTimeSpan(33, TimeUnit.minute), createTimeSpan(14, TimeUnit.second)),
				Arrays.asList(createTimeSpan(2, TimeUnit.second)),
				Arrays.asList(createTimeSpan(3, TimeUnit.minute), createTimeSpan(30, TimeUnit.second)),
				Arrays.asList(createTimeSpan(25, TimeUnit.hour), createTimeSpan(32, TimeUnit.minute), createTimeSpan(42, TimeUnit.second)),
				Arrays.asList(createTimeSpan(1, TimeUnit.hour), createTimeSpan(3, TimeUnit.second)),
				Arrays.asList(createTimeSpan(1, TimeUnit.minute), createTimeSpan(3, TimeUnit.milliSecond))
		);
		
		for (int i = 0; i < spans.length; i++) {
			TimeSpan ts = spans[i];
			List<TimeSpan> unitAnalysis = TimeSpanConversion.unitAnalysis(ts, 2, TimeUnit.hour, TimeUnit.milliSecond);
			List<TimeSpan> expectedUnits = expectedPartLists.get(i);
			
			assertThat(unitAnalysis.size() == expectedUnits.size()).isTrue();
			
			for (int e = 0; e < unitAnalysis.size(); e++) {
				TimeSpan ts1 = unitAnalysis.get(e);
				TimeSpan ts2 = expectedUnits.get(e);
				
				assertThat(equals(ts1, ts2)).isTrue();
			}
		}
	}
	
	private static boolean equals(TimeSpan ts1, TimeSpan ts2) {
		return ts1.getUnit() == ts2.getUnit() && ((Double)ts1.getValue()).equals(ts2.getValue()); 
	}

	// -----------------------------------------------------------------------
	// HELPER METHODS
	// -----------------------------------------------------------------------

	protected TimeSpan createTimeSpan(double value, TimeUnit timeUnit) {
		TimeSpan timeSpan = TimeSpan.T.create();
		timeSpan.setValue(value);
		timeSpan.setUnit(timeUnit);
		return timeSpan;
	}

}
