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
package com.braintribe.model.processing.query.support;

import org.junit.Test;

import com.braintribe.model.processing.query.fluent.PropertyQueryBuilder;
import com.braintribe.model.processing.query.test.model.Company;
import com.braintribe.model.query.PropertyQuery;
import com.braintribe.model.query.SelectQuery;
import com.braintribe.model.query.SimpleOrdering;
import com.braintribe.model.query.functions.ListIndex;
import com.braintribe.utils.junit.assertions.BtAssertions;

/**
 * Tests for {@link QueryConverter}
 */
public class QueryConverterTest {

	@Test
	public void convertsListQuery() throws Exception {
		PropertyQuery query = PropertyQueryBuilder.forProperty(Company.T, 1L, "persons").done();

		SelectQuery selectQuery = QueryConverter.convertPropertyQuery(query);

		SimpleOrdering ordering = (SimpleOrdering) selectQuery.getOrdering();

		BtAssertions.assertThat(ordering).isNotNull();
		BtAssertions.assertThat(ordering.getOrderBy()).isInstanceOf(ListIndex.class);
	}

}
