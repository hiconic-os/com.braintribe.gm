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
package com.braintribe.model.processing.query.test.stringifier;

import static com.braintribe.utils.SysPrint.spOut;

import org.junit.Test;

import com.braintribe.model.processing.query.fluent.EntityQueryBuilder;
import com.braintribe.model.processing.query.fluent.PropertyQueryBuilder;
import com.braintribe.model.processing.query.test.stringifier.model.ConfidentialEntity;
import com.braintribe.model.query.Query;
import com.braintribe.testing.junit.assertions.assertj.core.api.Assertions;

public class ConfidentialPropertyTests extends AbstractSelectQueryTests {

	private Query query;

	@Test
	public void simpleSelectQuery() {
		// @formatter:off
		 query = query()
				.from(ConfidentialEntity.T, "e")
				.where()
					.property("e", "password").eq("password123")
				.done();
		// @formatter:on

		assertResult("select * from com.braintribe.model.processing.query.test.stringifier.model.ConfidentialEntity e where e.password = ***");
	}

	@Test
	public void convolutedSelectQuery() {
		// @formatter:off
		query = query()
				.from(ConfidentialEntity.T, "e")
					.join("e","siblings", "ss")
					.join("ss","sibling", "s")
				.where()
					.property("s", "sibling.sibling.password").eq("password123")
				.done();
		// @formatter:on

		assertResult(
				"select * from com.braintribe.model.processing.query.test.stringifier.model.ConfidentialEntity e join e.siblings ss join ss.sibling s where s.sibling.sibling.password = ***");
	}

	@Test
	public void simpleEntityQuery() {
		// @formatter:off
		query = EntityQueryBuilder.from(ConfidentialEntity.T)
				.where()
					.property("password").eq("password123")
				.done();
		// @formatter:on

		assertResult("from com.braintribe.model.processing.query.test.stringifier.model.ConfidentialEntity where password = ***");
	}

	@Test
	public void simplePropertyQuery() {
		// @formatter:off
		query = PropertyQueryBuilder.forProperty(ConfidentialEntity.T, 1L, "siblings")
				.where()
					.property("password").eq("password123")
				.done();
		// @formatter:on

		assertResult(
				"property siblings of reference(com.braintribe.model.processing.query.test.stringifier.model.ConfidentialEntity, 1l) where password = ***");
	}

	private void assertResult(String expected) {
		String actual = query.stringify();
		spOut(actual);
		Assertions.assertThat(actual).isEqualTo(expected);
	}

}
