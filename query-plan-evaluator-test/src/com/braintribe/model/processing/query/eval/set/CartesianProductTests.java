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
import com.braintribe.model.processing.query.eval.set.base.ModelBuilder;
import com.braintribe.model.processing.query.test.model.Company;
import com.braintribe.model.processing.query.test.model.Person;
import com.braintribe.model.queryplan.set.CartesianProduct;
import com.braintribe.model.queryplan.set.SourceSet;

/**
 * Tests filtered set with the underlying source-set (for {@link Person} entities).
 */
public class CartesianProductTests extends AbstractEvalTupleSetTests {

	private Person pA, pB;
	private Company cA, cB;

	private SourceSet personSet;
	private SourceSet companySet;

	private CartesianProduct cartesianProduct;

	@Test
	public void firstOperandEmpty() throws Exception {
		buildCompany();
		buildQueryPlan();

		evaluate(cartesianProduct);

		assertNoMoreTuples();
	}

	@Test
	public void secondOperandEmpty() throws Exception {
		buildPerson();
		buildQueryPlan();

		evaluate(cartesianProduct);

		assertNoMoreTuples();
	}

	@Test
	public void productOftwoNonEmptySets() throws Exception {
		buildPerson();
		buildCompany();
		buildQueryPlan();

		evaluate(cartesianProduct);

		assertContainsTuple(pA, cA);
		assertContainsTuple(pA, cB);
		assertContainsTuple(pB, cA);
		assertContainsTuple(pB, cB);
		assertNoMoreTuples();
	}

	private void buildPerson() {
		registerAtSmood(pA = ModelBuilder.person("personA"));
		registerAtSmood(pB = ModelBuilder.person("personB"));
	}

	private void buildCompany() {
		registerAtSmood(cA = ModelBuilder.company("companyA"));
		registerAtSmood(cB = ModelBuilder.company("companyB"));
	}

	private void buildQueryPlan() {
		personSet = builder.sourceSet(Person.class);
		companySet = builder.sourceSet(Company.class);

		cartesianProduct = builder.cartesianProduct(personSet, companySet);
	}

}
