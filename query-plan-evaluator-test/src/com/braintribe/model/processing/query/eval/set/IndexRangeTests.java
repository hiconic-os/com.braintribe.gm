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

import org.junit.Before;
import org.junit.Test;

import com.braintribe.model.processing.query.eval.set.base.AbstractEvalTupleSetTests;
import com.braintribe.model.processing.query.eval.set.base.ModelBuilder;
import com.braintribe.model.processing.query.test.model.Person;
import com.braintribe.model.processing.smood.population.SmoodIndexTools;
import com.braintribe.model.queryplan.index.MetricIndex;
import com.braintribe.model.queryplan.index.RepositoryMetricIndex;

/**
 * 
 */
public class IndexRangeTests extends AbstractEvalTupleSetTests {

	private static final int PERSON_COUNT = 10;
	private final Person[] persons = new Person[PERSON_COUNT]; // personA to personH

	@Before
	public void buildData() {
		for (int i = 0; i < PERSON_COUNT; i++)
			registerAtSmood(persons[i] = ModelBuilder.person("person" + i));
	}

	@Test
	public void bothOpened() throws Exception {
		evaluate(builder.indexRange(Person.class, "indexedName", nameIndex(), "person2", false, "person7", false));

		for (int i = 3; i <= 6; i++)
			assertNextTuple(persons[i]);
		assertNoMoreTuples();
	}

	@Test
	public void leftClosed() throws Exception {
		evaluate(builder.indexRange(Person.class, "indexedName", nameIndex(), "person2", true, "person7", false));

		for (int i = 2; i <= 6; i++)
			assertNextTuple(persons[i]);
		assertNoMoreTuples();
	}

	@Test
	public void rightClosed() throws Exception {
		evaluate(builder.indexRange(Person.class, "indexedName", nameIndex(), "person2", false, "person7", true));

		for (int i = 3; i <= 7; i++)
			assertNextTuple(persons[i]);
		assertNoMoreTuples();
	}

	@Test
	public void bothClosed() throws Exception {
		evaluate(builder.indexRange(Person.class, "indexedName", nameIndex(), "person2", true, "person7", true));

		for (int i = 2; i <= 7; i++)
			assertNextTuple(persons[i]);
		assertNoMoreTuples();
	}

	@Test
	public void upperUnlimitedInclusive() throws Exception {
		evaluate(builder.indexRange(Person.class, "indexedName", nameIndex(), "person2", true, null, null));

		for (int i = 2; i < PERSON_COUNT; i++)
			assertNextTuple(persons[i]);
		assertNoMoreTuples();
	}

	@Test
	public void upperUnlimitedExclusive() throws Exception {
		evaluate(builder.indexRange(Person.class, "indexedName", nameIndex(), "person2", true, null, null));

		for (int i = 2; i < PERSON_COUNT; i++)
			assertNextTuple(persons[i]);
		assertNoMoreTuples();
	}

	@Test
	public void lowerUnlimitedInclusive() throws Exception {
		evaluate(builder.indexRange(Person.class, "indexedName", nameIndex(), null, true, "person7", true));

		for (int i = 0; i <= 7; i++)
			assertNextTuple(persons[i]);
		assertNoMoreTuples();
	}

	@Test
	public void lowerUnlimitedExclusive() throws Exception {
		evaluate(builder.indexRange(Person.class, "indexedName", nameIndex(), null, false, "person7", true));

		for (int i = 0; i <= 7; i++)
			assertNextTuple(persons[i]);
		assertNoMoreTuples();
	}

	@Test
	public void overallUnlimited() throws Exception {
		evaluate(builder.indexRange(Person.class, "indexedName", nameIndex(), null, null, null, null));

		for (int i = 0; i < PERSON_COUNT; i++)
			assertNextTuple(persons[i]);
		assertNoMoreTuples();
	}

	@Test
	public void nullKeys_SingleInheritanceLevel() throws Exception {
		for (int i = 0; i < PERSON_COUNT; i++)
			persons[i].setIndexedName(null);

		evaluate(builder.indexRange(Person.class, "indexedName", nameIndex(), null, null, null, null));
	}

	/** Having a hierarchy leads to RangesMerger being used. */
	@Test
	public void nullKeys_MultiInheritanceLevel() throws Exception {
		for (int i = 0; i < PERSON_COUNT; i++)
			persons[i].setIndexedName(null);

		registerAtSmood(ModelBuilder.owner("owner"));

		evaluate(builder.indexRange(Person.class, "indexedName", nameIndex(), null, null, null, null));
	}

	private MetricIndex nameIndex() {
		RepositoryMetricIndex index = RepositoryMetricIndex.T.create();
		index.setIndexId(SmoodIndexTools.indexId(Person.class.getName(), "indexedName"));

		return index;
	}

}
