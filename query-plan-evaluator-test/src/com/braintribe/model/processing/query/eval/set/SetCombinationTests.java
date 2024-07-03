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

import static com.braintribe.model.processing.query.eval.set.base.TupleSetBuilder.staticValue;
import static com.braintribe.model.processing.query.eval.set.base.TupleSetBuilder.valueComparison;
import static com.braintribe.model.processing.query.eval.set.base.TupleSetBuilder.valueProperty;

import org.junit.Before;
import org.junit.Test;

import com.braintribe.model.processing.query.eval.set.base.AbstractEvalTupleSetTests;
import com.braintribe.model.processing.query.eval.set.base.ModelBuilder;
import com.braintribe.model.processing.query.test.model.Person;
import com.braintribe.model.queryplan.filter.ConditionType;
import com.braintribe.model.queryplan.set.SourceSet;
import com.braintribe.model.queryplan.set.TupleSet;
import com.braintribe.model.queryplan.value.Value;

/**
 * Tests filtered set with the underlying source-set (for {@link Person} entities).
 */
public class SetCombinationTests extends AbstractEvalTupleSetTests {

	private Person pA, pB;
	private SourceSet personSet;
	private TupleSet filteredSet;
	private Value personNameValue;

	@Before
	public void buildData() {
		registerAtSmood(pA = ModelBuilder.person("personA"));
		registerAtSmood(pB = ModelBuilder.person("personB"));

		personSet = builder.sourceSet(Person.class);
		personNameValue = valueProperty(personSet, "name");
		filteredSet = builder.filteredSet(personSet, valueComparison(personNameValue, staticValue("personA"), ConditionType.equality));
	}

	@Test
	public void union() throws Exception {
		evaluate(builder.union(personSet, filteredSet));

		assertContainsTuple(pA);
		assertContainsTuple(pB);
		assertNoMoreTuples();
	}

	@Test
	public void unionCommuted() throws Exception {
		evaluate(builder.union(filteredSet, personSet));

		assertContainsTuple(pA);
		assertContainsTuple(pB);
		assertNoMoreTuples();
	}

	@Test
	public void intersection() throws Exception {
		evaluate(builder.intersection(personSet, filteredSet));

		assertContainsTuple(pA);
		assertNoMoreTuples();
	}

	@Test
	public void intersectionCommuted() throws Exception {
		evaluate(builder.intersection(filteredSet, personSet));

		assertContainsTuple(pA);
		assertNoMoreTuples();
	}

}
