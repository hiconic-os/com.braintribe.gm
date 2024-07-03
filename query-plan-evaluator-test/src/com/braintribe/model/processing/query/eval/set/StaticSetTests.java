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

import org.junit.Test;

import com.braintribe.model.generic.value.PersistentEntityReference;
import com.braintribe.model.processing.query.eval.set.base.AbstractEvalTupleSetTests;
import com.braintribe.model.processing.query.test.model.Person;
import com.braintribe.model.queryplan.set.StaticSet;

/**
 * 
 */
public class StaticSetTests extends AbstractEvalTupleSetTests {

	@Test
	public void emptyStaticSet() throws Exception {
		StaticSet set = builder.staticSet();
		evaluate(set);
		assertNoMoreTuples();
	}

	@Test
	public void nonEmptyStaticSet() throws Exception {
		StaticSet set = builder.staticSet("a", "b");

		evaluate(set);

		assertContainsTuple("a");
		assertContainsTuple("b");
		assertNoMoreTuples();
	}

	@Test
	public void staticSetOfEntities() throws Exception {
		Person p1 = b.person("P1").create();
		Person p2 = b.person("P2").create();

		StaticSet set = builder.staticSet(p1.reference(), p2.reference());

		evaluate(set);

		assertContainsTuple(p1);
		assertContainsTuple(p2);
		assertNoMoreTuples();
	}

	@Test
	public void staticSetOfUnknownEntities() throws Exception {
		PersistentEntityReference ref = b.person("P0").create().reference();
		ref.setRefId("unknown");

		StaticSet set = builder.staticSet(ref);

		evaluate(set);

		assertNoMoreTuples();
	}

}
