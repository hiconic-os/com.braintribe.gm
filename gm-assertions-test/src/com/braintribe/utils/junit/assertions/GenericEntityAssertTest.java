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
package com.braintribe.utils.junit.assertions;

import static com.braintribe.utils.junit.assertions.GmAssertions.assertThat;
import static com.braintribe.utils.junit.assertions.GmAssertions.property;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.braintribe.model.access.IncrementalAccess;
import com.braintribe.model.generic.pr.criteria.TraversingCriterion;
import com.braintribe.model.generic.processing.pr.fluent.TC;
import com.braintribe.model.generic.session.exception.GmSessionException;
import com.braintribe.model.processing.query.fluent.EntityQueryBuilder;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.query.EntityQuery;
import com.braintribe.testing.model.test.technical.features.SimpleEntity;
import com.braintribe.testing.model.test.testtools.TestModelTestTools;

public class GenericEntityAssertTest {

	@Test
	public void testPropertyIsAbsent() throws GmSessionException {
		final IncrementalAccess access = TestModelTestTools.newSmoodAccessMemoryOnly("test", TestModelTestTools.createTestModelMetaModel());
		final PersistenceGmSession generatingSession = TestModelTestTools.newSession(access);
		final String propertyName = "stringProperty";

		final SimpleEntity simpleEntity = generatingSession.create(SimpleEntity.T);
		generatingSession.commit();

		final PersistenceGmSession retrievingSession = TestModelTestTools.newSession(access);

		final TraversingCriterion tc = TC.create().pattern().entity(SimpleEntity.class).property(propertyName).close()
				.done();

		final EntityQuery query = EntityQueryBuilder.from(SimpleEntity.class).tc(tc).where().property("id")
				.eq(simpleEntity.getId()).done();
		final SimpleEntity retrieved = retrievingSession.query().entities(query).first();
		new GenericEntityAssert(retrieved).onProperty(propertyName).isAbsent();
		assertThat(retrieved).onProperty(propertyName).isAbsent();
		assertThat(property(retrieved, propertyName)).isAbsent();

		retrieved.getStringProperty();

		new GenericEntityAssert(retrieved).onProperty(propertyName).isNotAbsent();
		try {
			new GenericEntityAssert(retrieved).onProperty(propertyName).isAbsent();
			fail("Property '" + propertyName + "' of entity " + retrieved + " is absent!");
		} catch (final AssertionError e) {
			BtAssertions.assertThat(e.getMessage()).startsWith("Property '" + propertyName).endsWith("is not absent!");
		}
	}
}
