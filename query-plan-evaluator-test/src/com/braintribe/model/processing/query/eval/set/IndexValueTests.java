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

import static com.braintribe.model.processing.query.planner.builder.ValueBuilder.staticValue;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.braintribe.model.processing.query.eval.set.base.AbstractEvalTupleSetTests;
import com.braintribe.model.processing.query.eval.set.base.ModelBuilder;
import com.braintribe.model.processing.query.test.model.Company;
import com.braintribe.model.processing.query.test.model.Person;
import com.braintribe.model.processing.smood.population.SmoodIndexTools;
import com.braintribe.model.queryplan.index.Index;
import com.braintribe.model.queryplan.index.MetricIndex;
import com.braintribe.model.queryplan.index.RepositoryIndex;
import com.braintribe.model.queryplan.index.RepositoryMetricIndex;
import com.braintribe.model.queryplan.value.ConstantValue;
import com.braintribe.model.queryplan.value.IndexValue;

/**
 * 
 */
public class IndexValueTests extends AbstractEvalTupleSetTests {

	private Person pA1, pA2, pB;

	@Before
	public void buildData() {
		registerAtSmood(pA1 = ModelBuilder.person("personA"));
		registerAtSmood(pA2 = ModelBuilder.person("personA"));
		registerAtSmood(pB = ModelBuilder.person("personB"));
	}

	@Test
	public void findsCorrectEntity() throws Exception {
		evaluate(builder.indexSubSet(Person.class, "indexedName", nameIndex(), staticValue(Arrays.asList("personA"))));

		assertContainsTuple(pA1);
		assertContainsTuple(pA2);
		assertNoMoreTuples();
	}

	private MetricIndex nameIndex() {
		RepositoryMetricIndex index = RepositoryMetricIndex.T.create();
		index.setIndexId(SmoodIndexTools.indexId(Person.class.getName(), "indexedName"));

		return index;
	}

	/**
	 * Retrieving data for query like: <tt>select p from Person </tt>
	 */
	@Test
	public void findsCorrectEntityForIndexChain() throws Exception {
		Company c1, c2;

		registerAtSmood(c1 = ModelBuilder.company("company1"));
		registerAtSmood(c2 = ModelBuilder.company("company2"));

		pA1.setIndexedCompany(c1);
		pA2.setIndexedCompany(c1);
		pB.setIndexedCompany(c2);

		evaluate(builder.indexSubSet(Person.class, "indexedCompany", companyIndex(), indexValue(staticValue(Arrays.asList("company1")))));

		assertContainsTuple(pA1);
		assertContainsTuple(pA2);
		assertNoMoreTuples();
	}

	private Index companyIndex() {
		RepositoryIndex index = RepositoryIndex.T.create();
		index.setIndexId(SmoodIndexTools.indexId(Person.class.getName(), "indexedCompany"));

		return index;
	}

	private IndexValue indexValue(ConstantValue keys) {
		IndexValue result = IndexValue.T.create();
		result.setIndexId(SmoodIndexTools.indexId(Company.class.getName(), "indexedName"));
		result.setKeys(keys);

		return result;
	}

}
