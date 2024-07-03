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
package com.braintribe.testing.junit.assertions.gm.assertj.core.api;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.testing.junit.assertions.assertj.core.api.Assertions;

/**
 * Extends our {@link Assertions custom assertions} with additional Generic Model related assertions.
 *
 * @author michael.lafite
 */
public abstract class GmAssertions extends Assertions {

	/**
	 * Creates a {@link GenericEntity} assertion.
	 */
	public static GenericEntityAssert assertThat(GenericEntity actual) {
		return assertEntity(actual);
	}

	public static GenericEntityAssert assertEntity(GenericEntity actual) {
		return new GenericEntityAssert(actual);
	}

}
