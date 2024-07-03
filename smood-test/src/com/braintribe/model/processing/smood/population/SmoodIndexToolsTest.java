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
package com.braintribe.model.processing.smood.population;

import static com.braintribe.testing.junit.assertions.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class SmoodIndexToolsTest {

	@Test
	public void testIndexId() {
		assertThat(SmoodIndexTools.indexId("hello", "world")).isEqualTo("hello#world");
		assertThat(SmoodIndexTools.indexId((String) null, "world")).isEqualTo("null#world");
		assertThat(SmoodIndexTools.indexId("hello", null)).isEqualTo("hello#null");
		assertThat(SmoodIndexTools.indexId((String) null, null)).isEqualTo("null#null");
	}
	
}
