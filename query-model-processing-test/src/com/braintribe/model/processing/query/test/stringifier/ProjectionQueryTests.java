// ============================================================================
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
// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
// ============================================================================
package com.braintribe.model.processing.query.test.stringifier;

import org.junit.Assert;
import org.junit.Test;

import com.braintribe.model.processing.query.test.model.Owner;
import com.braintribe.model.processing.query.test.model.Person;
import com.braintribe.model.query.SelectQuery;

/**
 *
 */
public class ProjectionQueryTests extends AbstractSelectQueryTests {

	@Test
	public void selectingEntityAndProperty() {
		// @formatter:off
		 SelectQuery selectQuery = query()
				.from(Person.class, "_Person")
				.select("_Person")
				.select("_Person", "name")
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery);
		Assert.assertTrue(
				queryString.equalsIgnoreCase("select _Person, _Person.name from com.braintribe.model.processing.query.test.model.Person _Person"));
	}

	@Test
	public void selectingCompoundProperty() {
		// @formatter:off
		 SelectQuery selectQuery = query()
				.from(Owner.class, "_Owner")
				.select("_Owner")
				.select("_Owner", "company.name")
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery);
		Assert.assertTrue(queryString
				.equalsIgnoreCase("select _Owner, _Owner.company.name from com.braintribe.model.processing.query.test.model.Owner _Owner"));
	}

	@Test
	public void selectingConstants() {
		// @formatter:off
		 SelectQuery selectQuery = query()
				.from(Person.class, "_Person")
				.select("_Person")
				.select().value(99L)
				.select().value("constantString")
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery);
		Assert.assertTrue(queryString
				.equalsIgnoreCase("select _Person, 99l, 'constantString' from com.braintribe.model.processing.query.test.model.Person _Person"));
	}

	@Test
	public void selectingLocalizedValue() {
		// @formatter:off
		 SelectQuery selectQuery = query()
				.from(Person.class, "_Person")
				.select("_Person")
				.select().localize("pt").property("_Person", "localizedString")
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery);
		Assert.assertTrue(queryString.equalsIgnoreCase(
				"select _Person, localize(_Person.localizedString, 'pt') from com.braintribe.model.processing.query.test.model.Person _Person"));
	}

	@Test
	public void selectingMapKey() {
		// @formatter:off
		 SelectQuery selectQuery = query()
				.from(Owner.class, "_Owner")
				.join("_Owner", "companyMap", "_Company")
				.select().mapKey("_Company")
				.select("_Company")
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery);
		Assert.assertTrue(queryString.equalsIgnoreCase(
				"select mapKey(_Company), _Company from com.braintribe.model.processing.query.test.model.Owner _Owner join _Owner.companyMap _Company"));
	}

	@Test
	public void selectingMapValue() {
		// @formatter:off
		 SelectQuery selectQuery = query()
				.from(Owner.class, "_Owner")
				.select("_Owner", "name")
				.select("_Owner", "companyMap")
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery);
		Assert.assertTrue(queryString
				.equalsIgnoreCase("select _Owner.name, _Owner.companyMap from com.braintribe.model.processing.query.test.model.Owner _Owner"));
	}
}
