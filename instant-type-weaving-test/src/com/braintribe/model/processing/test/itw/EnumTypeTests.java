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
package com.braintribe.model.processing.test.itw;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.processing.ImportantItwTestSuperType;
import com.braintribe.model.processing.test.itw.entity.Color;

/**
 * Tests for methods of {@link EntityType}
 */
public class EnumTypeTests extends ImportantItwTestSuperType {

	@Test
	public void getEnumValue() {
		assertThat(Color.T.getEnumValue("red")).isSameAs(Color.red);
	}

	@Test
	public void assignability() {
		assertThat(Color.T.isAssignableFrom(Color.T)).isTrue();
	}

	@Test
	public void instanceofChecking() {
		assertThat(Color.T.isInstance("string")).isFalse();
		assertThat(Color.T.isInstance(Color.red)).isTrue();
	}

}
