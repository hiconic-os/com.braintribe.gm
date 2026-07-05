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

import static com.braintribe.model.access.security.query.PasswordPropertyTools.HIDDEN_PASSWORD;
import static com.braintribe.utils.lcd.CollectionTools2.first;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;

import com.braintribe.model.access.security.testdata.query.EntityWithProps;
import com.braintribe.model.processing.query.fluent.SelectQueryBuilder;
import com.braintribe.model.processing.query.tools.PreparedTcs;
import com.braintribe.model.query.SelectQuery;
import com.braintribe.model.record.ListRecord;
import com.braintribe.utils.junit.assertions.BtAssertions;
import com.braintribe.utils.junit.core.rules.ThrowableChainRule;

/**
 * 
 */
public class ItilsCloningTests extends AbstractQueryingTest {

	private EntityWithProps entity;

	@Rule
	public ThrowableChainRule exceptionChainRule = new ThrowableChainRule();

	@Override
	protected void prepareData() {
		entity = delegateSession.create(EntityWithProps.T);
		entity.setPassword("doesn't matter, this will not be retrieved");
	}

	@Test
	public void selectQuery_passwordSelectionAmongOthers_Tc() throws Exception {
		SelectQuery query = new SelectQueryBuilder() //
				.select("e", "id") //
				.select("e", "partition") //
				.select("e", "password") //
				.from(EntityWithProps.class, "e") //
				.tc(PreparedTcs.scalarOnlyTc) //
				.done();
		List<?> queryResult = query(query);

		BtAssertions.assertThat(queryResult).hasSize(1);

		ListRecord lr = first(queryResult);

		BtAssertions.assertThat(lr.getValues().get(0)).isNotNull();
		BtAssertions.assertThat(lr.getValues().get(1)).isNotNull();
		BtAssertions.assertThat(lr.getValues().get(2)).isEqualTo(HIDDEN_PASSWORD);
	}

}
