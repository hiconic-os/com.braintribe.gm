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
package com.braintribe.model.processing.core.expert.impl;

import static com.braintribe.testing.junit.assertions.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.manipulation.Manipulation;
import com.braintribe.model.generic.manipulation.ManipulationComment;
import com.braintribe.model.generic.reflection.EntityType;

/**
 * Tests for {@link PolymorphicDenotationMultiMap}
 */
public class PolymorphicDenotationMultiMapTests {

	private final PolymorphicDenotationMultiMap<GenericEntity, String> map = new PolymorphicDenotationMultiMap<>();

	@Test
	public void emptyMap() throws Exception {
		assertConfigSize(0);

		assertThat(map.<String> find(ManipulationComment.T)).isNull();
		assertSizeFor(ManipulationComment.T, 0);
		assertSizeFor(Manipulation.T, 0);
	}

	@Test
	public void singleElement() throws Exception {
		map.put(Manipulation.T, "Manipulation");

		assertConfigSize(1);

		assertSizeFor(ManipulationComment.T, 1);
		assertSizeFor(Manipulation.T, 1);
		assertSizeFor(GenericEntity.T, 0);
	}

	@Test
	public void multipleElements() throws Exception {
		map.put(ManipulationComment.T, "ManipulationComment");
		map.put(Manipulation.T, "Manipulation1");
		map.put(Manipulation.T, "Manipulation2");
		map.put(GenericEntity.T, "GenericEntity");

		assertConfigSize(4);

		assertSizeFor(ManipulationComment.T, 4);
		assertSizeFor(Manipulation.T, 3);
		assertSizeFor(GenericEntity.T, 1);
	}

	@Test
	public void removeWorks() throws Exception {
		map.put(ManipulationComment.T, "ManipulationComment 1");
		map.put(ManipulationComment.T, "ManipulationComment 2");
		map.put(ManipulationComment.T, "ManipulationComment 3");
		map.put(Manipulation.T, "Manipulation");

		assertConfigSize(4);
		assertSizeFor(ManipulationComment.T, 4);

		map.removeEntry(ManipulationComment.T, "Non-Existent Value");
		assertConfigSize(4);
		assertSizeFor(ManipulationComment.T, 4);

		map.removeEntry(ManipulationComment.T, "ManipulationComment 1");
		assertConfigSize(3);
		assertSizeFor(ManipulationComment.T, 3);

		// removes remaining 2 values
		map.remove(ManipulationComment.T);
		assertConfigSize(1);
		assertSizeFor(ManipulationComment.T, 1);

	}

	private void assertSizeFor(EntityType<?> type, int size) {
		assertThat(map.findAll(type)).hasSize(size);
	}

	private void assertConfigSize(int size) {
		assertThat(map.configurationSize()).isEqualTo(size);
	}

}
