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
package com.braintribe.model.processing.vde.impl.root;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.braintribe.model.bvd.time.Now;
import com.braintribe.model.generic.value.Escape;
import com.braintribe.model.generic.value.ValueDescriptor;
import com.braintribe.model.processing.vde.impl.VDGenerator;
import com.braintribe.model.processing.vde.test.VdeTest;

/**
 * Provides tests for {@link Escape}.
 * 
 */
public class EscapeVdeTest extends VdeTest {

	public static VDGenerator $ = new VDGenerator(); 
	
	/**
	 * Validate that a {@link Escape} that contains {@link ValueDescriptor}, will evaluate to the same
	 * {@link ValueDescriptor}.
	 */
	@Test
	public void testNonNullEvaluation() throws Exception {

		// init test data
		final Escape vd = $.escape(); 
		final Now randomVd = $.now();
		vd.setValue(randomVd);

		// validate input

		// validate that data is not null
		assertThat(randomVd).isNotNull();
		assertThat(vd).isNotNull();

		// run the evaluate method
		Object result =  evaluate(vd);

		// validate output
		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(ValueDescriptor.class);

		assertThat(result).isEqualTo(randomVd);
		assertThat(result).isNotEqualTo(vd);
	}

	/**
	 * Validate that a {@link Escape} that contains null, will evaluate to null.
	 */
	@Test
	public void testNullEvaluation() throws Exception {

		// init test data
		final Escape vd = $.escape();

		// validate input
		// validate that data is not null
		assertThat(vd).isNotNull();

		// run the evaluate method
		Object result =  evaluate(vd);

		// validate output
		assertThat(result).isNull();
	}

}
