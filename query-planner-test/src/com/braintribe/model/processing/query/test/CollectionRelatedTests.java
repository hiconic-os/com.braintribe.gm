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

import org.junit.Test;

import com.braintribe.model.processing.query.eval.api.RuntimeQueryEvaluationException;
import com.braintribe.model.processing.query.test.model.Company;
import com.braintribe.model.processing.query.test.model.Owner;
import com.braintribe.model.processing.query.test.model.Person;
import com.braintribe.model.query.SelectQuery;
import com.braintribe.model.queryplan.filter.In;
import com.braintribe.model.queryplan.set.FilteredSet;
import com.braintribe.model.queryplan.set.MergeLookupJoin;
import com.braintribe.model.queryplan.set.Projection;
import com.braintribe.model.queryplan.set.join.EntityJoin;
import com.braintribe.model.queryplan.set.join.JoinKind;
import com.braintribe.model.queryplan.set.join.MapJoin;
import com.braintribe.model.queryplan.set.join.SetJoin;
import com.braintribe.model.queryplan.value.TupleComponent;

/**
 * 
 */
public class CollectionRelatedTests extends AbstractQueryPlannerTests {

	@Test
	public void selectingCollection_Directly() {
		// @formatter:off
		SelectQuery selectQuery = query()
				.from(Person.T, "p")
				.select("p", "nicknames")
				.done();
		// @formatter:on

		runTest(selectQuery);

		// @formatter:off
		assertQueryPlan()
		.hasType(Projection.T)
			.whereOperand()
				.hasType(SetJoin.T)
				.whereOperand().isSourceSet_(Person.T)
				.whereProperty("valueProperty").isValueProperty_("nicknames")
			.close()
			.hasValues(1)
				.whereElementAt(0).isTupleComponent_(1)
		;
		// @formatter:on
	}

	@Test
	public void selectingCollection_ByJoining() {
		// @formatter:off
		SelectQuery selectQuery = query()
				.from(Person.T, "p")
					.join("p", "nicknames", "n")
				.select("n")
				.done();
		// @formatter:on

		runTest(selectQuery);

		// @formatter:off
		assertQueryPlan()
		.hasType(Projection.T)
			.whereOperand()
				.hasType(SetJoin.T)
				.whereOperand().isSourceSet_(Person.T)
				.whereProperty("valueProperty").isValueProperty_("nicknames")
			.close()
			.hasValues(1)
				.whereElementAt(0).isTupleComponent_(1)
		;
		// @formatter:on
	}

	@Test
	public void selectingCollection_DirectlyAndByJoining() {
		// @formatter:off
		SelectQuery selectQuery = query()
				.from(Person.T, "p")
					.join("p", "nicknames", "n")
				.select("p", "nicknames")
				.done();
		// @formatter:on

		runTest(selectQuery);

		// @formatter:off
		assertQueryPlan()
		.hasType(Projection.T)
			.whereOperand()
				.hasType(SetJoin.T)
				.whereOperand()
					.hasType(SetJoin.T)
					.whereOperand().isSourceSet_(Person.T)
					.whereProperty("valueProperty").isValueProperty_("nicknames")
				.close()
			.close()
			.hasValues(1)
				.whereElementAt(0)
					.hasType(TupleComponent.T)
					.whereProperty("tupleComponentIndex") // We do not know the number, read below
		;
		// @formatter:on

		// TODO OPTIMIZATION
		/* tupleComponentIndex is 1 or 2, depending on how the join is taken - because there are two joins, but 1 is
		 * irrelevant, and will not be performed, but the planner assigns it a tuple position, as the detection that it
		 * is useless is only done later. */
	}

	@Test
	public void selectingCollection_DirectlyTwiceCausingCartesianProduct() {
		// @formatter:off
		SelectQuery selectQuery = query()
				.from(Person.T, "p")
				.select("p", "nicknames")
				.select("p", "nicknames")
				.done();
		// @formatter:on

		runTest(selectQuery);

		// @formatter:off
		assertQueryPlan()
		.hasType(Projection.T)
			.whereOperand()
				.hasType(SetJoin.T)
				.whereOperand().hasType(SetJoin.T)
					.whereOperand().isSourceSet_(Person.T)
					.whereProperty("valueProperty").isValueProperty_("nicknames")
				.close()
			.close()
			.hasValues(2).whenOrderedBy("tupleComponentIndex")
				.whereElementAt(0).isTupleComponent_(1)
				.whereElementAt(1).isTupleComponent_(2)
		;
		// @formatter:on
	}

	@Test
	public void selectingCollectionUsingInCondition() {
		// @formatter:off
		SelectQuery selectQuery = query()
				.from(Person.T, "p")
					.select("p", "name")
					.where()
						.value("bob").in().property("p", "nicknames")
				.done();
		// @formatter:on

		runTest(selectQuery);

		// @formatter:off
		assertQueryPlan()
		.hasType(Projection.T)
			.whereOperand()
				.hasType(FilteredSet.T)
				.whereOperand().isSourceSet_(Person.T)
				.whereProperty("filter")
					.hasType(In.T)
					.whereProperty("leftOperand")
						.isStaticValue_("bob")
					.whereProperty("rightOperand").isValueProperty_("nicknames")
		;
		// @formatter:on
	}

	@Test
	public void selectingEntityConditionOnCollectionGivenByPath() {
		// @formatter:off
		SelectQuery selectQuery = query()
				.from(Owner.class, "o")
				.where()
					.value("p1").in().property("o", "company.personNameSet")
				.done();
		// @formatter:on

		runTest(selectQuery);

		// @formatter:off
		assertQueryPlan()
		.hasType(Projection.T)
			.whereOperand()
				.hasType(FilteredSet.T)
				.whereOperand()
					.hasType(EntityJoin.T)
					.whereProperty("joinKind").is_(JoinKind.left)
					.whereProperty("valueProperty").isValueProperty_("company")
					.whereOperand().isSourceSet_(Owner.T)
				.close()
				.whereProperty("filter")
					.hasType(In.T)
					.whereProperty("leftOperand").isStaticValue_("p1")
					.whereProperty("rightOperand")
						.isValueProperty_("personNameSet")
		;
		// @formatter:on
	}

	@Test
	public void joiningWithConditionOnMapKey() {
		// @formatter:off
		SelectQuery selectQuery = query()
				.from(Owner.class, "o")
					.join("o", "companyValueMap", "map")
				.from(Company.T, "c")
				.select("c", "name")
				.where()
					.mapKey("map").eq().entity("c")
				.done();
		// @formatter:on

		runTest(selectQuery);

		// @formatter:off
		assertQueryPlan()
			.hasType(Projection.T).whereOperand()
				.hasType(MergeLookupJoin.T)				
				.whereOperand().hasType(MapJoin.T)
					.whereProperty("valueProperty").isValueProperty_("companyValueMap")
					.whereOperand().isSourceSet_(Owner.T)
				.close()
				.whereProperty("otherOperand").isSourceSet_(Company.T)
		;
		// @formatter:on
	}

	@Test(expected = RuntimeQueryEvaluationException.class)
	public void selectingCollectionProperty() {
		// @formatter:off
		SelectQuery selectQuery = query()
				.from(Owner.class, "p")
					.select("p", "companySet.name")
				.done();
		// @formatter:on

		runTest(selectQuery);
	}

}
