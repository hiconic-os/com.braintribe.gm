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
package com.braintribe.model.processing.query.test.building;

import org.junit.Test;

import com.braintribe.model.processing.query.building.EntityQueries;
import com.braintribe.model.processing.query.test.model.Person;
import com.braintribe.model.query.CascadedOrdering;
import com.braintribe.model.query.EntityQuery;
import com.braintribe.model.query.Ordering;
import com.braintribe.model.query.OrderingDirection;
import com.braintribe.testing.junit.assertions.assertj.core.api.Assertions;

public class QueryBuildingTest {

	@Test
	public void testMultiOrdering() {
		EntityQuery query = TestEntityQueries.multiOrdering();

		Ordering ordering = query.getOrdering();

		Assertions.assertThat(ordering).isInstanceOf(CascadedOrdering.class);

		CascadedOrdering cascadedOrdering = (CascadedOrdering) ordering;
		Assertions.assertThat(cascadedOrdering.getOrderings().size()).isEqualTo(2);
	}

	private static class TestEntityQueries extends EntityQueries {
		public static EntityQuery multiOrdering() {
			return from(Person.T) //
					.orderBy(OrderingDirection.ascending, property("indexedName")) //
					.orderBy(OrderingDirection.ascending, property("companyName"));
		}
	}

}
