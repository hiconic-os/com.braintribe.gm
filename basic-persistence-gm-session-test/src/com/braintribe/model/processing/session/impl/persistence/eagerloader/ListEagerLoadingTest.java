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

import static com.braintribe.utils.lcd.CollectionTools2.asList;
import static com.braintribe.utils.lcd.CollectionTools2.newList;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.braintribe.model.processing.session.impl.persistence.eagerloader.model.EagerItem;
import com.braintribe.model.processing.session.impl.persistence.eagerloader.model.EagerOwner;

/**
 * @see AbstractEagerLoaderTest
 * 
 * @author peter.gazdik
 */
public class ListEagerLoadingTest extends AbstractEagerLoaderTest {

	/**
	 * Test eager loading for a {@code List<String>} property, where every entity has a value for that property.
	 */
	@Test
	public void simpleList_Full() throws Exception {
		for (int i = 0; i < NUMBER_OF_OWNERS; i++)
			newOwner(i, o -> o.setStringList(simpleListFor(o)));

		loadOwners();

		for (EagerOwner owner : owners) {
			List<String> stringList = owner.getStringList();
			Assertions.assertThat(stringList).isEqualTo(simpleListFor(owner));
		}

		assertJustOneQuery();
	}

	private List<String> simpleListFor(EagerOwner owner) {
		Integer id = owner.getId();
		return asList("X_" + id, "Y_" + id, "Z_" + id);
	}

	/**
	 * Test eager loading for a {@code List<String>} property, where for some entities the property is empty.
	 */
	@Test
	public void simpleList_Incomplete() throws Exception {
		for (int i = 0; i < NUMBER_OF_OWNERS; i++)
			newOwner(i, o -> o.setStringList(simpleListIfEvenId(o)));

		loadOwners();

		for (EagerOwner owner : owners) {
			List<String> stringList = owner.getStringList();
			Assertions.assertThat(stringList).isEqualTo(simpleListIfEvenId(owner));
		}

		assertJustOneQuery();
	}

	private List<String> simpleListIfEvenId(EagerOwner owner) {
		int id = (Integer) owner.getId();

		return id % 2 == 1 ? newList() : simpleListFor(owner);
	}

	/**
	 * Test eager loading for a {@code List<GenericEntity>} property, where every entity has a value for that property.
	 */
	@Test
	public void entityList() throws Exception {
		for (int i = 0; i < NUMBER_OF_OWNERS; i++)
			newOwner(i, o -> o.setEntityList(itemsListFor(o)));

		loadOwners();

		for (EagerOwner owner : owners) {
			List<EagerItem> entityList = owner.getEntityList();

			Assertions.assertThat(entityList).hasSize(3);
			for (int i = 0; i < 3; i++)
				Assertions.assertThat(entityList.get(i).getName()).isEqualTo(getItemName(owner, i));
		}

		assertJustOneQuery();
	}

	private List<EagerItem> itemsListFor(EagerOwner owner) {
		return asList(newItemOf(owner, 0), newItemOf(owner, 1), newItemOf(owner, 2));
	}

}
