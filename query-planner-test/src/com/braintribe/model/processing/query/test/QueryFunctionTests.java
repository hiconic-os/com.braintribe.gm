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

import com.braintribe.model.processing.query.test.model.Person;
import com.braintribe.model.query.SelectQuery;
import com.braintribe.model.query.functions.value.AsString;
import com.braintribe.model.queryplan.filter.Like;
import com.braintribe.model.queryplan.set.FilteredSet;
import com.braintribe.model.queryplan.set.Projection;
import com.braintribe.model.queryplan.value.QueryFunctionValue;

/**
 * 
 */
public class QueryFunctionTests extends AbstractQueryPlannerTests {

	@Test
	public void asStringCondition() {
		// @formatter:off
		SelectQuery selectQuery = query()
				.from(Person.T, "p")
				.where()
					.asString().property("p", "id").like(".*")
				.done();
		// @formatter:on

		runTest(selectQuery);

		// @formatter:off
		assertQueryPlan().hasType(Projection.T)
			.whereOperand().hasType(FilteredSet.T)
				.whereOperand().isSourceSet_(Person.T)
				.whereProperty("filter").hasType(Like.T)
					.whereProperty("leftOperand").hasType(QueryFunctionValue.T)
						.whereProperty("queryFunction").hasType(AsString.T) .close()
					.close()
					.whereProperty("rightOperand").isStaticValue_(".*")
		;
		// @formatter:on
	}

}
