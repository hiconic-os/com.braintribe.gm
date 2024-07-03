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

import static com.braintribe.model.generic.GenericEntity.globalId;

import org.junit.Test;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.processing.query.eval.api.repo.IndexInfo;
import com.braintribe.model.processing.query.test.model.Owner;
import com.braintribe.model.processing.query.test.model.Person;
import com.braintribe.model.query.SelectQuery;

public class GlobalIdFilteringQueryTests extends AbstractSelectQueryTests {

	/** @see #globalIdEquality_ConcreteType() */
	@Test
	public void globalIdEquality_GenericEntity() {
		Person p;
		p = b.person("Jack").globalId("p-0").create();
		p = b.person("John").globalId("p-1").create();

		// @formatter:off
		SelectQuery selectQuery = query()
				.from(GenericEntity.T, "p")
				.where()
					.property("p", globalId).eq("p-1")
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertNextResult(p);
		assertNoMoreResults();
	}

	/**
	 * This is different from {@link #globalIdEquality_GenericEntity()} because here we are querying for a concrete
	 * type. This requires special handling from Smood, because internally it only implements one index for all
	 * entities, i.e. it is done on the {@link GenericEntity} level. This is relevant when resolving the
	 * {@link IndexInfo}.
	 */
	@Test
	public void globalIdEquality_ConcreteType() {
		Person p;
		p = b.person("Jack").globalId("p-0").create();
		p = b.person("John").globalId("p-1").create();

		// @formatter:off
		SelectQuery selectQuery = query()
				.from(Person.T, "p")
				.where()
					.property("p", globalId).eq("p-1")
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertNextResult(p);
		assertNoMoreResults();
	}

	@Test
	public void globalIdEquality_WrongType_NoResult() {
		b.person("Jack").globalId("p-0").create();
		b.person("John").globalId("p-1").create();

		// @formatter:off
		SelectQuery selectQuery = query()
				.from(Owner.T, "p")
				.where()
					.property("p", globalId).eq("p-1")
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertNoMoreResults();
	}
}
