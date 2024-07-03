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
package com.braintribe.model.processing.query.test;

import static com.braintribe.model.generic.GenericEntity.globalId;
import static com.braintribe.utils.lcd.CollectionTools2.asSet;

import org.junit.Test;

import com.braintribe.model.processing.query.test.model.Person;
import com.braintribe.model.query.SelectQuery;
import com.braintribe.model.queryplan.index.RepositoryIndex;
import com.braintribe.model.queryplan.set.IndexSubSet;
import com.braintribe.model.queryplan.set.Projection;

/**
 * 
 */
public class GlobalIdFilteringTests extends AbstractQueryPlannerTests {

	@Test
	public void globalIdEquality() {
		// @formatter:off
		SelectQuery selectQuery = query()
				.from(Person.T, "p")
				.where()
					.property("p", globalId).eq("p-1")
				.done();
		// @formatter:on

		runTest(selectQuery);

		// @formatter:off
		assertQueryPlan()
			.hasType(Projection.T).whereOperand()
				.hasType(IndexSubSet.T)
				.whereProperty("typeSignature").is_(Person.class.getName())
				.whereProperty("propertyName").is_(globalId)
				.whereProperty("keys")
					.isStaticSet_("p-1")
				.whereProperty("lookupIndex")
					.hasType(RepositoryIndex.T)
				.close()
		;
		// @formatter:on
	}

	@Test
	public void globalIdEquality_ReverseOperandOrder() {
		// @formatter:off
		SelectQuery selectQuery = query()
				.from(Person.T, "p")
				.where()
				 	.value("p-1").eq().property("p", globalId)
				.done();
		// @formatter:on

		runTest(selectQuery);

		// @formatter:off
		assertQueryPlan()
			.hasType(Projection.T).whereOperand()
				.hasType(IndexSubSet.T)
				.whereProperty("typeSignature").is_(Person.T.getTypeSignature())
				.whereProperty("propertyName").is_(globalId)
				.whereProperty("keys")
					.isStaticSet_("p-1")
				.whereProperty("lookupIndex")
					.hasType(RepositoryIndex.T)
				.close()
		;
		// @formatter:on
	}

	@Test
	public void globalIdEquality_InSet() {
		// @formatter:off
		SelectQuery selectQuery = query()
				.from(Person.T, "p")
				.where()
					.property("p", globalId).in(asSet("p-1", "p-2"))
				.done();
		// @formatter:on

		runTest(selectQuery);

		// @formatter:off
		assertQueryPlan()
			.hasType(Projection.T).whereOperand()
				.hasType(IndexSubSet.T)
				.whereProperty("typeSignature").is_(Person.T.getTypeSignature())
				.whereProperty("propertyName").is_(globalId)
				.whereProperty("keys")
					.isStaticSet_("p-1", "p-2")
				.whereProperty("lookupIndex")
					.hasType(RepositoryIndex.T)
				.close()
		;
		// @formatter:on
	}

	@Test
	public void globalIdEquality_Disjunction() {
		// @formatter:off
		SelectQuery selectQuery = query()
				.from(Person.T, "p")
				.where()
				.disjunction()
					.property("p", "globalId").eq("p-1")
					.property("p", "globalId").eq("p-2")
				.close()
				.done();
		// @formatter:on

		runTest(selectQuery);

		// @formatter:off
		assertQueryPlan()
			.hasType(Projection.T).whereOperand()
				.hasType(IndexSubSet.T)
				.whereProperty("typeSignature").is_(Person.T.getTypeSignature())
				.whereProperty("propertyName").is_(globalId)
				.whereProperty("keys")
					.isStaticSet_("p-1", "p-2")
				.whereProperty("lookupIndex")
					.hasType(RepositoryIndex.T)
				.close()
		;
		// @formatter:on
	}

}
