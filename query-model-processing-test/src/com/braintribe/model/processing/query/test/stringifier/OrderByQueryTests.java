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
package com.braintribe.model.processing.query.test.stringifier;

import org.junit.Assert;
import org.junit.Test;

import com.braintribe.model.processing.query.test.model.Owner;
import com.braintribe.model.processing.query.test.model.Person;
import com.braintribe.model.query.JoinType;
import com.braintribe.model.query.OrderingDirection;
import com.braintribe.model.query.SelectQuery;

/**
 *
 */
public class OrderByQueryTests extends AbstractSelectQueryTests {

	@Test
	public void simpleAscendingSort() {
		// @formatter:off
		 SelectQuery selectQuery = query()
				.from(Person.class, "_Person")
				.orderBy(OrderingDirection.ascending).property("_Person", "name")
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery);
		Assert.assertTrue(queryString
				.equalsIgnoreCase("select * from com.braintribe.model.processing.query.test.model.Person _Person order by _Person.name asc"));
	}

	@Test
	public void simpleDescendingSort() {
		// @formatter:off
		 SelectQuery selectQuery = query()
				.from(Person.class, "_Person")
				.orderBy(OrderingDirection.descending).property("_Person", "name")
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery);
		Assert.assertTrue(queryString
				.equalsIgnoreCase("select * from com.braintribe.model.processing.query.test.model.Person _Person order by _Person.name desc"));
	}

	@Test
	public void compoundProperty() {
		// @formatter:off
		 SelectQuery selectQuery = query()
				.from(Owner.class, "_Owner")
				.orderBy().property("_Owner", "company.name")
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery);
		Assert.assertTrue(queryString
				.equalsIgnoreCase("select * from com.braintribe.model.processing.query.test.model.Owner _Owner order by _Owner.company.name asc"));
	}

	@Test
	public void multipleOrderBys() {
		// @formatter:off
		 SelectQuery selectQuery = query()
				.from(Owner.class, "_Owner")
				.orderByCascade()
				.dir(OrderingDirection.descending).property("_Owner", "company.name")
				.dir(OrderingDirection.ascending).value(45) // this must be ignored
				.dir(OrderingDirection.ascending).property("_Owner", "name")
				.close()
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery);
		Assert.assertTrue(queryString.equalsIgnoreCase(
				"select * from com.braintribe.model.processing.query.test.model.Owner _Owner order by _Owner.company.name desc, 45 asc, _Owner.name asc"));
	}

	@Test
	public void multipleOrderBysWithProjection() {
		// @formatter:off
		 SelectQuery selectQuery = query()
				.from(Owner.class, "_Owner")
				.select("_Owner", "company.name")
				.select("_Owner", "name")
				.select("_Owner")
				.orderByCascade()
				.dir(OrderingDirection.descending).property("_Owner", "company.name")
				.dir(OrderingDirection.ascending).value(45) // this must be ignored
				.dir(OrderingDirection.ascending).property("_Owner", "name")
				.close()
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery);
		Assert.assertTrue(queryString.equalsIgnoreCase(
				"select _Owner.company.name, _Owner.name, _Owner from com.braintribe.model.processing.query.test.model.Owner _Owner order by _Owner.company.name desc, 45 asc, _Owner.name asc"));
	}

	@Test
	public void orderByWithPagination() {
		// @formatter:off
		 SelectQuery selectQuery = query()
				.from(Person.class, "_Person")
				.orderBy().property("_Person", "name")
				.paging(1, 2)
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery);
		Assert.assertTrue(queryString.equalsIgnoreCase(
				"select * from com.braintribe.model.processing.query.test.model.Person _Person order by _Person.name asc limit 1 offset 2"));
	}

	@Test
	public void orderByWithPaginationAndMoreExistingResults() {
		// @formatter:off
		 SelectQuery selectQuery = query()
				.from(Person.class, "_Person")
				.orderBy().property("_Person", "name")
				.paging(1, 2)
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery);
		Assert.assertTrue(queryString.equalsIgnoreCase(
				"select * from com.braintribe.model.processing.query.test.model.Person _Person order by _Person.name asc limit 1 offset 2"));
	}

	/**
	 * Same as {@link #orderByWithPaginationAndMoreExistingResults() }, but adds a join and select clause. There was a
	 * bug.
	 */
	@Test
	public void orderByWithPaginationAndJoinAndMoreExistingResults() {
		// @formatter:off
		 SelectQuery selectQuery = query()
				.from(Person.class, "_Person")
				.join("_Person", "company", "_Company", JoinType.left)
				.select("_Person")
				.where()
				.entity("_Company").eq(null)
				.orderBy().property("_Person", "name")
				.paging(1, 2)
				.done();
		// @formatter:on

		String queryString = stringify(selectQuery);
		Assert.assertTrue(queryString.equalsIgnoreCase(
				"select _Person from com.braintribe.model.processing.query.test.model.Person _Person left join _Person.company _Company where _Company = null order by _Person.name asc limit 1 offset 2"));
	}
}
