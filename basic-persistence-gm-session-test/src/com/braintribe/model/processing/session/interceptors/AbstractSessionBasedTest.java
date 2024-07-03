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
package com.braintribe.model.processing.session.interceptors;

import org.junit.Before;

import com.braintribe.common.lcd.EmptyReadWriteLock;
import com.braintribe.model.access.ModelAccessException;
import com.braintribe.model.access.NonIncrementalAccess;
import com.braintribe.model.access.smood.basic.SmoodAccess;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.criteria.TraversingCriterion;
import com.braintribe.model.generic.processing.pr.fluent.TC;
import com.braintribe.model.generic.reflection.GenericModelTypeReflection;
import com.braintribe.model.generic.session.exception.GmSessionException;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.processing.model.tools.MetaModelTools;
import com.braintribe.model.processing.query.fluent.EntityQueryBuilder;
import com.braintribe.model.processing.session.impl.persistence.BasicPersistenceGmSession;
import com.braintribe.model.processing.session.test.data.Flag;
import com.braintribe.model.processing.session.test.data.Person;
import com.braintribe.model.query.EntityQuery;

/**
 * @author peter.gazdik
 */
public abstract class AbstractSessionBasedTest {

	protected GenericModelTypeReflection typeReflection = GMF.getTypeReflection();

	protected SmoodAccess smoodAccess;
	protected BasicPersistenceGmSession session;
	protected BasicPersistenceGmSession lastNewSession;

	@Before
	public void setup() {
		GmMetaModel metaModel = MetaModelTools.provideRawModel(Person.T, Flag.T);
		
		smoodAccess = new SmoodAccess();
		smoodAccess.setDataDelegate(new NonIncrementalAccess() {

			@Override
			public GmMetaModel getMetaModel() {
				return metaModel;
			}

			@Override
			public void storeModel(Object model) throws ModelAccessException {
				// do nothing
			}

			@Override
			public Object loadModel() throws ModelAccessException {
				return null;
			}
		});
		smoodAccess.setReadWriteLock(EmptyReadWriteLock.INSTANCE);

		session = newPersistenceSession();
	}

	protected BasicPersistenceGmSession newPersistenceSession() {
		BasicPersistenceGmSession session = new BasicPersistenceGmSession(smoodAccess);

		return session;
	}

	protected Person newPerson(String name) {
		Person p = session.create(Person.T);
		p.setName(name);

		return p;
	}

	protected Person querySinglePerson() throws GmSessionException {
		return querySinglePerson(null);
	}

	protected Person querySinglePerson(TraversingCriterion tc) throws GmSessionException {
		lastNewSession = newPersistenceSession();

		EntityQuery query = EntityQueryBuilder.from(Person.class).done();
		query.setTraversingCriterion(tc);

		return lastNewSession.query().entities(query).<Person> first();
	}

	protected TraversingCriterion noPropertiesTC() {
		// @formatter:off
		return TC.create()
				.negation()
					.disjunction()
						.property(GenericEntity.id)
						.property(GenericEntity.partition)
						.property(GenericEntity.globalId)
					.close()
				.done();
		// @formatter:on
	}

}
