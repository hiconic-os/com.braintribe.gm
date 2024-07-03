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

import org.junit.Test;

import com.braintribe.model.processing.query.eval.set.base.AbstractEvalTupleSetTests;
import com.braintribe.model.processing.query.eval.set.base.ModelBuilder;
import com.braintribe.model.processing.query.test.model.Person;
import com.braintribe.model.queryplan.set.Projection;
import com.braintribe.model.queryplan.set.SourceSet;
import com.braintribe.model.queryplan.set.TupleSet;
import com.braintribe.model.queryplan.value.Value;

/**
 * Tests filtered set with the underlying source-set (for {@link Person} entities).
 */
public class DistinctSetTests extends AbstractEvalTupleSetTests {

	private TupleSet distinctSet;

	@Test
	public void distinct() throws Exception {
		buildData();

		evaluate(distinctSet);

		assertContainsTuple("p1");
		assertContainsTuple("p2");
		assertNoMoreTuples();
	}

	private void buildData() {
		registerAtSmood(ModelBuilder.person("p1"));
		registerAtSmood(ModelBuilder.person("p1"));
		registerAtSmood(ModelBuilder.person("p2"));
		registerAtSmood(ModelBuilder.person("p2"));
		registerAtSmood(ModelBuilder.person("p2"));

		SourceSet personSet = builder.sourceSet(Person.class);
		Value personNameValue = valueProperty(personSet, "name");
		Projection personNameSet = builder.projection(personSet, personNameValue);
		distinctSet = builder.distinctSet(personNameSet);
	}

}
