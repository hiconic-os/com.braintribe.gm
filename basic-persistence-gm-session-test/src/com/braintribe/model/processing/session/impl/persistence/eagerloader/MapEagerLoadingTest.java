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

import static com.braintribe.utils.lcd.CollectionTools2.asMap;
import static com.braintribe.utils.lcd.CollectionTools2.newMap;

import java.util.Map;
import java.util.Map.Entry;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.braintribe.model.processing.session.impl.persistence.eagerloader.model.EagerItem;
import com.braintribe.model.processing.session.impl.persistence.eagerloader.model.EagerOwner;

/**
 * @see AbstractEagerLoaderTest
 * 
 * @author peter.gazdik
 */
public class MapEagerLoadingTest extends AbstractEagerLoaderTest {

	/**
	 * Test eager loading for a {@code Map<String>} property, where every entity has a value for that property.
	 */
	@Test
	public void simpleMap_Full() throws Exception {
		for (int i = 0; i < NUMBER_OF_OWNERS; i++)
			newOwner(i, o -> o.setIntegerStringMap(simpleMapFor(o)));

		loadOwners();

		for (EagerOwner owner : owners) {
			Map<Integer, String> stringMap = owner.getIntegerStringMap();
			Assertions.assertThat(stringMap).isEqualTo(simpleMapFor(owner));
		}

		assertJustOneQuery();
	}

	private Map<Integer, String> simpleMapFor(EagerOwner owner) {
		Integer id = owner.getId();
		return asMap(0, "X_" + id, 1, "Y_" + id, 2, "Z_" + id);
	}

	/**
	 * Test eager loading for a {@code Map<GenericEntity>} property, where every entity has a value for that property.
	 */
	@Test
	public void entityMap() throws Exception {
		for (int i = 0; i < NUMBER_OF_OWNERS; i++)
			newOwner(i, o -> o.setEntityMap(itemsMapFor(o)));

		loadOwners();

		for (EagerOwner owner : owners) {
			Map<EagerItem, EagerItem> entityMap = owner.getEntityMap();

			Assertions.assertThat(entityMap).hasSize(3);

			Map<String, String> itemNameMap = newMap();
			for (Entry<EagerItem, EagerItem> entry : entityMap.entrySet()) {
				EagerItem key = entry.getKey();
				EagerItem value = entry.getValue();

				itemNameMap.put(key.getName(), value.getName());
			}

			Assertions.assertThat(itemNameMap).isEqualTo(itemsNameMapFor(owner));
		}

		assertJustOneQuery();
	}

	private Map<EagerItem, EagerItem> itemsMapFor(EagerOwner owner) {
		EagerItem item0 = newItemOf(owner, 0);
		EagerItem item1 = newItemOf(owner, 1);
		EagerItem item2 = newItemOf(owner, 2);
		return asMap(item0, item0, item1, item1, item2, item2);
	}

	private Object itemsNameMapFor(EagerOwner owner) {
		String item0 = getItemName(owner, 0);
		String item1 = getItemName(owner, 1);
		String item2 = getItemName(owner, 2);
		return asMap(item0, item0, item1, item1, item2, item2);
	}

}
