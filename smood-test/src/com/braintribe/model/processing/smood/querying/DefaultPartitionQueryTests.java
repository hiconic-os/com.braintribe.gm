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

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.processing.query.test.model.Person;
import com.braintribe.model.query.SelectQuery;

/**
 * 
 */
public class DefaultPartitionQueryTests extends AbstractSelectQueryTests {

	private static final String DEFAULT_PARTITION = "default";
	private static final String CUSTOM_PARTITION = "custom";

	@Override
	protected void postConstruct() {
		smood.setDefaultPartition(DEFAULT_PARTITION);
	}

	@Test
	public void queryDefaultPartition() {
		b.person("Person").create();

		// @formatter:off
		SelectQuery selectQuery = query()
				.select("p", GenericEntity.partition)
				.from(Person.T, "p")
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertResultContains(DEFAULT_PARTITION);
		assertNoMoreResults();
	}

	@Test
	public void queryWithPartitionCondition_WhenTrue() {
		Person p;
		p = b.person("Person").create();

		// @formatter:off
		SelectQuery selectQuery = query()
				.select("p")
				.from(Person.T, "p")
				.where()
					.property("p", GenericEntity.partition).eq(DEFAULT_PARTITION)
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertResultContains(p);
		assertNoMoreResults();
	}

	@Test
	public void queryWithPartitionCondition_WhenFalse() {
		b.person("Person").create();

		// @formatter:off
		SelectQuery selectQuery = query()
				.select("p")
				.from(Person.T, "p")
				.where()
					.property("p", GenericEntity.partition).eq(CUSTOM_PARTITION)
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertNoMoreResults();
	}
}
