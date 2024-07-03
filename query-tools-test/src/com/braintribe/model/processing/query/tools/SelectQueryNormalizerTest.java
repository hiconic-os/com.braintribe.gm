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
package com.braintribe.model.processing.query.tools;

import static com.braintribe.utils.junit.assertions.BtAssertions.assertThat;
import static org.fest.assertions.Assertions.assertThat;

import java.util.Set;

import org.junit.Test;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.processing.query.fluent.SelectQueryBuilder;
import com.braintribe.model.query.From;
import com.braintribe.model.query.SelectQuery;
import com.braintribe.model.query.conditions.ValueComparison;

/**
 * 
 */
public class SelectQueryNormalizerTest {

	@Test
	public void selectionsAreAdded() throws Exception {
		SelectQuery query = query().from(GenericEntity.T, "ge").done();
		assertThat(query.getSelections()).isNullOrEmpty();

		query = normalize(query);
		assertThat(query.getSelections()).hasSize(1);

		Object from = query.getSelections().get(0);
		assertThat(from.getClass()).isAssignableTo(From.class);
		assertThat(((From) from).getEntityTypeSignature()).isEqualTo(GenericEntity.class.getName());
	}

	/**
	 * There was a bug that would cause that the original query was modified (as collections were not cloned but the
	 * original value was taken).
	 */
	@Test
	public void originalQueryNotChanged() throws Exception {
		// @formatter:off
		SelectQuery query = query()
				.select("m", "types")
				.from(GmMetaModel.T, "m")
				.where()
					.property("m", "id").eq(1l)
				.done();
		// @formatter:on

		assertThat(query.getFroms().get(0).getJoins()).isNullOrEmpty();

		SelectQuery normalizedQuery = normalize(query);

		assertThat(query.getFroms().get(0).getJoins()).isNullOrEmpty();
		assertThat(normalizedQuery.getFroms().get(0).getJoins()).hasSize(1);
	}

	/**
	 * Since there is now way to express an empty collection as a GM value in many cases, we should generally treat null
	 * as an empty collection in a context where it makes sense.
	 */
	@Test
	public void inOperatorIsNullSafe() {
		// @formatter:off
		SelectQuery query = query()
				.from(GmMetaModel.T, "p")
					.select("p", "name")
					.where()
						.value("bob").in(null)
				.done();
		// @formatter:on

		query = normalize(query);

		ValueComparison vc = (ValueComparison) query.getRestriction().getCondition();
		assertThat(vc.getRightOperand()).isInstanceOf(Set.class);
	}

	private SelectQuery normalize(SelectQuery query) {
		return new SelectQueryNormalizer(query, false, false).normalize();
	}

	private SelectQueryBuilder query() {
		return new SelectQueryBuilder();
	}

}
