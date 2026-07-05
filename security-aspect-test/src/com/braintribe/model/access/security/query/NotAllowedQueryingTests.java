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

import org.junit.Rule;
import org.junit.Test;

import com.braintribe.exception.AuthorizationException;
import com.braintribe.model.access.security.testdata.query.EntityWithProps;
import com.braintribe.model.access.security.testdata.query.NonQueryableEntity;
import com.braintribe.model.access.security.testdata.query.NonVisibleEntity;
import com.braintribe.model.processing.aop.api.interceptor.InterceptionException;
import com.braintribe.model.processing.query.fluent.EntityQueryBuilder;
import com.braintribe.model.processing.query.fluent.PropertyQueryBuilder;
import com.braintribe.model.query.EntityQuery;
import com.braintribe.model.query.PropertyQuery;
import com.braintribe.utils.junit.core.rules.ThrowableChain;
import com.braintribe.utils.junit.core.rules.ThrowableChainRule;

/**
 * Tests that in all these cases the query will not be allowed.
 * <p>
 * Note that I am taking advantage of {@link ThrowableChainRule}, so hopefully the tests will make sense once you read
 * what it's about.
 */
public class NotAllowedQueryingTests extends AbstractQueryingTest {

	@Rule
	public ThrowableChainRule exceptionChainRule = new ThrowableChainRule(InterceptionException.class);

	@Test
	public void nonQueryableEntityNotOk() {
		EntityQuery query = EntityQueryBuilder.from(NonQueryableEntity.class).done();

		aopAccess.queryEntities(query);
	}

	@Test
	public void nonQueryableEntityPropertyQueryNotOk() {
		PropertyQuery query = PropertyQueryBuilder.forProperty(NonQueryableEntity.T, 1L, "id").done();

		aopAccess.queryProperty(query);
	}

	@Test
	@ThrowableChain({ AuthorizationException.class })
	public void nonVisibleEntityNotOk() {
		EntityQuery query = EntityQueryBuilder.from(NonVisibleEntity.class).done();

		aopAccess.queryEntities(query);
	}

	@Test
	@ThrowableChain({})
	public void visibleModelOk() throws Throwable {
		EntityQuery query = EntityQueryBuilder.from(EntityWithProps.class).done();

		aopAccess.queryEntities(query);
	}

	@Test
	@ThrowableChain({ AuthorizationException.class })
	public void nonVisibleModelNotOk() {
		setUserRoles(MODEL_IGNORER);

		EntityQuery query = EntityQueryBuilder.from(EntityWithProps.class).done();

		aopAccess.queryEntities(query);
	}
}
