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
public class ConcatenationTests extends AbstractEvalTupleSetTests {

	private Person p;

	@Test
	public void testConcatenation() throws Exception {
		buildData();

		SourceSet set = builder.sourceSet(Person.class);

		evaluate(builder.concatenation(set, set));
		assertNextTuple(p);
		assertNextTuple(p);
		assertNoMoreTuples();

	}

	private void buildData() {
		registerAtSmood(p = instantiate(Person.class));
	}
}
