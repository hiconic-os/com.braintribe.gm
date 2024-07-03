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

import static com.braintribe.model.processing.query.eval.set.base.TupleSetBuilder.valueProperty;

import org.junit.Before;
import org.junit.Test;

import com.braintribe.model.processing.query.eval.set.base.AbstractEvalTupleSetTests;
import com.braintribe.model.processing.query.eval.set.base.ModelBuilder;
import com.braintribe.model.processing.query.test.model.Person;
import com.braintribe.model.queryplan.set.PaginatedSet;
import com.braintribe.model.queryplan.set.SourceSet;
import com.braintribe.model.queryplan.set.TupleSet;
import com.braintribe.model.queryplan.value.Value;

/**
 * If tests are failing, check {@link OrderedSetTests}. This also uses ordering so that tests make sense, so this is only a problem iff ordering is
 * working correctly.
 */
public class PaginatedSetTests extends AbstractEvalTupleSetTests {

	private static final int PERSON_COUNT = 10;
	private final Person[] persons = new Person[PERSON_COUNT];

	private SourceSet personSet;
	private Value personNameValue;

	@Before
	public void buildData() {
		for (int i = 0; i < PERSON_COUNT; i++)
			registerAtSmood(persons[i] = ModelBuilder.person("person" + String.valueOf((char) (i + 65))));

		personSet = builder.sourceSet(Person.class);
		personNameValue = valueProperty(personSet, "name");
	}

	// ##################################
	// ## . . . VALUE COMPARISONS . . .##
	// ##################################

	@Test
	public void fullRangeFromStart() throws Exception {
		evaluate(paginatedSet(2, 0));

		assertNextTuple(persons[0]);
		assertNextTuple(persons[1]);
		assertNoMoreTuples();
	}

	@Test
	public void fullRangeInTheMiddle() throws Exception {
		evaluate(paginatedSet(2, 2));

		assertNextTuple(persons[2]);
		assertNextTuple(persons[3]);
		assertNoMoreTuples();
	}

	@Test
	public void fullRangeAtTheEnd() throws Exception {
		evaluate(paginatedSet(2, 8));

		assertNextTuple(persons[8]);
		assertNextTuple(persons[9]);
		assertNoMoreTuples();
	}

	@Test
	public void cutRangeAtTheEnd() throws Exception {
		evaluate(paginatedSet(2, 9));

		assertNextTuple(persons[9]);
		assertNoMoreTuples();
	}

	@Test
	public void limitIsZero() throws Exception {
		int OFFSET = 2;

		evaluate(paginatedSet(0, OFFSET));

		for (int i = OFFSET; i < PERSON_COUNT; i++)
			assertNextTuple(persons[i]);
		assertNoMoreTuples();
	}

	private PaginatedSet paginatedSet(int limit, int offset) {
		TupleSet orderedSet = builder.orderedSet(personSet, personNameValue, false);
		return builder.paginatedSet(orderedSet, limit, offset);
	}

}
