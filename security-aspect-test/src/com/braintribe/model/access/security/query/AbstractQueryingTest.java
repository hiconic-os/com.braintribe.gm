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
package com.braintribe.model.access.security.query;

import java.util.List;

import org.fest.assertions.Assertions;

import com.braintribe.model.access.security.common.AbstractSecurityAspectTest;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.query.EntityQuery;
import com.braintribe.model.query.EntityQueryResult;
import com.braintribe.model.query.PropertyQuery;
import com.braintribe.model.query.PropertyQueryResult;
import com.braintribe.model.query.SelectQuery;
import com.braintribe.model.query.SelectQueryResult;

public class AbstractQueryingTest extends AbstractSecurityAspectTest {

	// ####################################
	// ## . . . . . Assertions . . . . . ##
	// ####################################

	/** Executes given query and checks the number of entities returned. */
	protected List<GenericEntity> assertReturnedEntities(EntityQuery query, int expectedCount) {
		if (userRoles == null)
			throw new RuntimeException("Error in test - no user role defined.");

		List<GenericEntity> entities = queryEntities(query);
		Assertions.assertThat(entities).isNotNull().as("Wrong number of entities returned from the query").hasSize(expectedCount);
		return entities;
	}

	protected void assertResultIsEmpty(EntityQuery query) {
		List<GenericEntity> entities = queryEntities(query);
		Assertions.assertThat(entities).isNotNull().isEmpty();
	}

	protected List<GenericEntity> queryEntities(EntityQuery query) {
		EntityQueryResult queryResult = aopAccess.queryEntities(query);
		return queryResult.getEntities();
	}

	protected void assertQueriedPropertyNull(PropertyQuery query) {
		Object result = queryProperty(query);
		Assertions.assertThat(result).isNull();
	}

	protected void assertQueriedProperty(PropertyQuery query, Object value) {
		Object result = queryProperty(query);
		Assertions.assertThat(result).isEqualTo(value);
	}

	protected Object queryProperty(PropertyQuery query) {
		PropertyQueryResult result = aopAccess.queryProperty(query);
		return result.getPropertyValue();
	}

	protected List<?> query(SelectQuery query) {
		SelectQueryResult queryResult = aopAccess.query(query);
		return queryResult.getResults();
	}

}
