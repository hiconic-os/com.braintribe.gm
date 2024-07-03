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
package com.braintribe.model.processing.smood.querying;

import org.junit.Test;

import com.braintribe.model.processing.query.test.model.Person;
import com.braintribe.model.query.SelectQuery;

/**
 * 
 */
public class IndexedFilteringWhenMetaModelAddedLaterTests extends AbstractSelectQueryTests {

	/**
	 * Like this we initialize the smood without meta-data.
	 */
	@Override
	protected void setSmoodMetaModel() {
		// Do not do anything
	}

	/**
	 * Same as {@link IndexedFilteringQueryTests#singleSourceFindForIndexInt()}, but we only add the meta-data later.
	 */
	@Test
	public void singleSourceFindForIndexInt() {
		Person p;

		p = b.person("P1").indexedInteger(5).create();
		p = b.person("P2").indexedInteger(45).create();

		smood.setMetaModel(super.provideEnrichedMetaModel());

		// like this we make sure the index exists, if the previous method did not initialize it
		b.person("P2").indexedInteger(70).create();

		// @formatter:off
		SelectQuery selectQuery = query()
				.from(Person.T, "person")
				.where()
				.property("person", "indexedInteger").eq(45)
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertResultContains(p);
		assertNoMoreResults();
	}

}
