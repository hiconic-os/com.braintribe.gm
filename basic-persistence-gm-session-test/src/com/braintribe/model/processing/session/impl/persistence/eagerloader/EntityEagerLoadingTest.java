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

import static com.braintribe.utils.lcd.CollectionTools2.first;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.braintribe.model.processing.session.impl.persistence.eagerloader.model.EagerItem;
import com.braintribe.model.processing.session.impl.persistence.eagerloader.model.EagerOwner;

/**
 * @see AbstractEagerLoaderTest
 * 
 * @author peter.gazdik
 */
public class EntityEagerLoadingTest extends AbstractEagerLoaderTest {

	/**
	 * Test eager loading for a entity property, where every entity has a value for that property.
	 */
	@Test
	public void entity_Full() throws Exception {
		for (int i = 0; i < NUMBER_OF_OWNERS; i++)
			newOwner(i, o -> o.setEntity(newItemOf(o, 0)));

		loadOwners();

		for (EagerOwner owner : owners) {
			EagerItem entity = owner.getEntity();

			Assertions.assertThat(entity).isNotNull();
			Assertions.assertThat(entity.getName()).isEqualTo(getItemName(owner, 0));
		}

		assertJustOneQuery();
	}

	/**
	 * Test eager loading for a entity property, where every entity has a value for that property.
	 */
	@Test
	public void entity_Full_TripleSize() throws Exception {
		int nOwners = 2 * NUMBER_OF_OWNERS + (NUMBER_OF_OWNERS / 2);

		for (int i = 0; i < nOwners; i++)
			newOwner(i, o -> o.setEntity(newItemOf(o, 0)));

		loadOwners();

		first(owners).getEntity();
		Assertions.assertThat(queryCounter.totalCount).isEqualTo(3);

		for (EagerOwner owner : owners) {
			EagerItem entity = owner.getEntity();

			Assertions.assertThat(entity).isNotNull();
			Assertions.assertThat(entity.getName()).isEqualTo(getItemName(owner, 0));
		}
	}

}
