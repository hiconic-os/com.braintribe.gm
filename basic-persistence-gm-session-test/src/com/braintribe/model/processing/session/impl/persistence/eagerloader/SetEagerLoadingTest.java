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
package com.braintribe.model.processing.session.impl.persistence.eagerloader;

import static com.braintribe.utils.lcd.CollectionTools2.asSet;
import static com.braintribe.utils.lcd.CollectionTools2.newSet;

import java.util.Set;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.braintribe.model.processing.session.impl.persistence.eagerloader.model.EagerItem;
import com.braintribe.model.processing.session.impl.persistence.eagerloader.model.EagerOwner;

/**
 * @see AbstractEagerLoaderTest
 * 
 * @author peter.gazdik
 */
public class SetEagerLoadingTest extends AbstractEagerLoaderTest {

	/**
	 * Test eager loading for a {@code Set<String>} property, where every entity has a value for that property.
	 */
	@Test
	public void simpleSet_Full() throws Exception {
		for (int i = 0; i < NUMBER_OF_OWNERS; i++)
			newOwner(i, o -> o.setStringSet(simpleSetFor(o)));

		loadOwners();

		for (EagerOwner owner : owners) {
			Set<String> stringSet = owner.getStringSet();
			Assertions.assertThat(stringSet).isEqualTo(simpleSetFor(owner));
		}

		assertJustOneQuery();
	}

	private Set<String> simpleSetFor(EagerOwner owner) {
		Integer id = owner.getId();
		return asSet("X_" + id, "Y_" + id, "Z_" + id);
	}

	/**
	 * Test eager loading for a {@code Set<String>} property, where for some entities the property is empty.
	 */
	@Test
	public void simpleSet_Incomplete() throws Exception {
		for (int i = 0; i < NUMBER_OF_OWNERS; i++)
			newOwner(i, o -> o.setStringSet(simpleSetIfEvenId(o)));

		loadOwners();

		for (EagerOwner owner : owners) {
			Set<String> stringSet = owner.getStringSet();
			Assertions.assertThat(stringSet).isEqualTo(simpleSetIfEvenId(owner));
		}

		assertJustOneQuery();
	}

	private Set<String> simpleSetIfEvenId(EagerOwner owner) {
		int id = (Integer) owner.getId();

		return id % 2 == 1 ? newSet() : simpleSetFor(owner);
	}

	/**
	 * Test eager loading for a {@code Set<GenericEntity>} property, where every entity has a value for that property.
	 */
	@Test
	public void entitySet() throws Exception {
		for (int i = 0; i < NUMBER_OF_OWNERS; i++)
			newOwner(i, o -> o.setEntitySet(itemsSetFor(o)));

		loadOwners();

		for (EagerOwner owner : owners) {
			Set<EagerItem> entitySet = owner.getEntitySet();

			Assertions.assertThat(entitySet).hasSize(3);

			Set<String> itemNames = entitySet.stream().map(EagerItem::getName).collect(Collectors.toSet());
			Assertions.assertThat(itemNames).isEqualTo(//
					asSet(getItemName(owner, 0), getItemName(owner, 1), getItemName(owner, 2)) //
			);
		}

		assertJustOneQuery();
	}

	private Set<EagerItem> itemsSetFor(EagerOwner owner) {
		return asSet(newItemOf(owner, 0), newItemOf(owner, 1), newItemOf(owner, 2));
	}

}
