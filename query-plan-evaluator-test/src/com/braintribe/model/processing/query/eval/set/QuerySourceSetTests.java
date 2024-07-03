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
package com.braintribe.model.processing.query.eval.set;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

import com.braintribe.common.lcd.EmptyReadWriteLock;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.processing.query.eval.api.repo.DelegatingRepository;
import com.braintribe.model.processing.query.eval.set.base.AbstractEvalTupleSetTests;
import com.braintribe.model.processing.query.test.model.Company;
import com.braintribe.model.processing.query.test.model.Person;
import com.braintribe.model.processing.smood.Smood;
import com.braintribe.model.query.Ordering;
import com.braintribe.model.query.conditions.Condition;
import com.braintribe.model.queryplan.set.QuerySourceSet;

/**
 * 
 */
public class QuerySourceSetTests extends AbstractEvalTupleSetTests {

	private Person p1, p2;

	// ###############################################
	// ## . . . DelegatingRepo Initialization . . . ##
	// ###############################################

	@Override
	protected Smood newSmood() {
		return new EntityQuerySmood();
	}

	private class EntityQuerySmood extends Smood implements DelegatingRepository {
		public EntityQuerySmood() {
			super(EmptyReadWriteLock.INSTANCE);
		}

		@Override
		public Iterable<? extends GenericEntity> provideEntities(String ts, Condition condition, Ordering ordering) {
			return Person.class.getName().equals(ts) ? Arrays.asList(p1, p2) : Collections.<GenericEntity> emptySet();
		}

		@Override
		public boolean supportsFulltextSearch() {
			return false;
		}
	}

	// ###############################################
	// ## . . . . . . . Test methods . . . . . . . .##
	// ###############################################

	@Test
	public void emptySourceSet() throws Exception {
		QuerySourceSet set = builder.querySourceSet(Company.class);

		evaluate(set);

		assertNoMoreTuples();
	}

	@Test
	public void testEvaluateSourceSet() throws Exception {
		buildData();

		QuerySourceSet set = builder.querySourceSet(Person.class);
		evaluate(set);
		assertContainsTuple(p1);
		assertContainsTuple(p2);
	}

	private void buildData() {
		registerAtSmood(p1 = instantiate(Person.class));
		registerAtSmood(p2 = instantiate(Person.class));
	}
}
