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

import com.braintribe.model.processing.query.eval.set.base.AbstractEvalTupleSetTests;
import com.braintribe.model.processing.query.test.model.Person;
import com.braintribe.model.queryplan.set.SourceSet;

/**
 * 
 */
public class SourceSetTests extends AbstractEvalTupleSetTests {

	private Person p1, p2;

	@Test
	public void emptySourceSet() throws Exception {
		SourceSet set = builder.sourceSet(Person.class);
		evaluate(set);
		assertNoMoreTuples();
	}

	@Test
	public void testEvaluateSourceSet() throws Exception {
		buildData();

		SourceSet set = builder.sourceSet(Person.class);

		evaluate(set);
		assertContainsTuple(p1);
		assertContainsTuple(p2);
	}

	private void buildData() {
		registerAtSmood(p1 = instantiate(Person.class));
		registerAtSmood(p2 = instantiate(Person.class));
	}
}
