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
package com.braintribe.model.processing.smood;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.braintribe.model.processing.query.test.model.Company;
import com.braintribe.model.processing.query.test.model.Owner;
import com.braintribe.model.processing.query.test.model.Person;
import com.braintribe.model.processing.session.api.notifying.NotifyingGmSession;
import com.braintribe.model.processing.smood.test.AbstractSmoodTests;

/**
 * 
 */
public class Smood_Delete_Test extends AbstractSmoodTests {

	NotifyingGmSession session;

	@Override
	protected void postConstruct() {
		session = smood.getGmSession();
	}

	@Test
	public void dropReference_Entity() {
		Company c1 = b.company("c1").create();
		Person p1 = b.person("p1").company(c1).create();

		smood.deleteEntity(c1);

		assertThat(p1.getCompany()).isNull();
	}

	/** This differs from previous one in the Owner.company is an inherited property. */
	@Test
	public void dropReference_Entity_PropertyInherited() {
		Company c1 = b.company("c1").create();
		Owner o1 = b.owner("o1").company(c1).create();

		smood.deleteEntity(c1);

		assertThat(o1.getCompany()).isNull();
	}

	@Test
	public void dropReference_List() {
		Company c1 = b.company("c1").create();
		Owner o1 = b.owner("o1").addToCompanyList(c1).create();

		assertThat(o1.getCompanyList()).isNotEmpty();

		smood.deleteEntity(c1);

		assertThat(o1.getCompanyList()).isEmpty();
	}

	@Test
	public void dropReference_Set() {
		Company c1 = b.company("c1").create();
		Owner o1 = b.owner("o1").addToCompanySet(c1).create();

		assertThat(o1.getCompanySet()).isNotEmpty();

		smood.deleteEntity(c1);

		assertThat(o1.getCompanySet()).isEmpty();
	}

	@Test
	public void dropReference_MapKey() {
		Company c1 = b.company("c1").create();
		Owner o1 = b.owner("o1").addToCompanyValueMap(c1, 1).create();

		assertThat(o1.getCompanyValueMap()).isNotEmpty();

		smood.deleteEntity(c1);

		assertThat(o1.getCompanyValueMap()).isEmpty();
	}

	@Test
	public void dropReference_MapValue() {
		Company c1 = b.company("c1").create();
		Owner o1 = b.owner("o1").addToCompanyMap("c1", c1).create();

		assertThat(o1.getCompanyMap()).isNotEmpty();

		smood.deleteEntity(c1);

		assertThat(o1.getCompanyMap()).isEmpty();
	}

}
