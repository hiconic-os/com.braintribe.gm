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
package com.braintribe.model.processing.smood.querying;

import static com.braintribe.utils.lcd.CollectionTools2.asSet;

import java.util.Collections;

import org.junit.Test;

import com.braintribe.model.generic.reflection.EnumType;
import com.braintribe.model.generic.value.EnumReference;
import com.braintribe.model.processing.query.test.model.Color;
import com.braintribe.model.processing.query.test.model.Company;
import com.braintribe.model.processing.query.test.model.Owner;
import com.braintribe.model.processing.query.test.model.Person;
import com.braintribe.model.query.SelectQuery;

/**
 * 
 */
public class SimpleSelectQueryTests extends AbstractSelectQueryTests {

	/** SeeBasicUseCaseTests#singleSourceNoCondition() */
	@Test
	public void singleSourceNoCondition() {
		Person p1 = b.person("Mr First").create();
		Person p2 = b.person("Mr Second").create();

		// @formatter:off
		SelectQuery selectQuery = query()
				.from(Person.T, "person")
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertResultContains(p1);
		assertResultContains(p2);
		assertNoMoreResults();
	}

	/**
	 * Checks entity signature for 'final ' type (without sub-types).
	 */
	@Test
	public void sourceType_Final() {
		b.owner("Mr Second").create();

		// @formatter:off
		SelectQuery selectQuery = query()
				.select().entitySignature().entity("o")
				.from(Owner.T, "o")
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertResultContains(Owner.class.getName());
		assertNoMoreResults();
	}

	/**
	 * Checks entity signature for types from a hierarchy.
	 */
	@Test
	public void sourceType_Hierarchy() {
		b.person("Mr First").create();
		b.owner("Mr Second").create();

		// @formatter:off
		SelectQuery selectQuery = query()
				.select().entitySignature().entity("p")
				.from(Person.T, "p")
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertResultContains(Person.class.getName());
		assertResultContains(Owner.class.getName());
		assertNoMoreResults();
	}

	/**
	 * Checks entity signature for types from a hierarchy.
	 */
	@Test
	public void conditionOnSourceType() {
		Person p;
		p = b.person("Mr First").create();
		p = b.owner("Mr Second").create();

		// @formatter:off
		SelectQuery selectQuery = query()
				.from(Person.T, "p")
				.select().entity("p")
				.where()
					.entitySignature("p").eq(Owner.class.getName())
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertResultContains(p);
		assertNoMoreResults();
	}

	/** SeeBasicUseCaseTests#sameSourceTwiceNoCondition() */
	@Test
	public void sameSourceTwiceNoCondition() {
		Person p1 = b.person("Mr First").create();
		Person p2 = b.person("Mr Second").create();

		// @formatter:off
		SelectQuery selectQuery = query()
				.from(Person.T, "p1")
				.from(Person.T, "p2")
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertResultContains(p1, p1);
		assertResultContains(p1, p2);
		assertResultContains(p2, p1);
		assertResultContains(p2, p2);
		assertNoMoreResults();
	}

	/** SeeBasicUseCaseTests#singleSourceNonIndexCondition() */
	@Test
	public void singleSourceNonIndexCondition() {
		Person p;

		p = b.person("Jack").create();
		p = b.person("John").create();

		// @formatter:off
		SelectQuery selectQuery = query()
				.from(Person.T, "p")
				.where()
					.property("p", "name").eq("John")
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertResultContains(p);
		assertNoMoreResults();
	}

	/** SeeBasicUseCaseTests#singleSourceNonIndexConditionOnEntityProperty() */
	@Test
	public void singleSourceConditionOnEntityProperty() {
		Company c1 = b.company("C1").create();
		Company c2 = b.company("C2").create();

		Person p;

		p = b.owner("P1").company(c1).create();
		p = b.owner("P2").company(c2).create();

		// @formatter:off
		SelectQuery selectQuery = query()
				.from(Person.T, "p")
				.where()
					.property("p", "company").eq().entity(c2)
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertResultContains(p);
		assertNoMoreResults();
	}

	@Test
	public void singleSourceConditionOnEntityProperty2() {
		Company c1 = b.company("C1").create();
		Company c2 = b.company("C2").create();

		Person p;

		p = b.owner("P1").company(c1).create();
		p = b.owner("P2").company(c2).create();

		// @formatter:off
		SelectQuery selectQuery = query()
				.from(Person.T, "p")
				.where()
					.property("p", "company").eq().value(c2)
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertResultContains(p);
		assertNoMoreResults();
	}

	@Test
	public void singleSourceConditionOnPropertyPath() {
		Company c1 = b.company("C1").create();
		Company c2 = b.company("C2").create();

		Person p;

		p = b.owner("P1").company(c1).create();
		p = b.owner("P2").company(c2).create();

		// @formatter:off
		SelectQuery selectQuery = query()
				.from(Person.T, "p")
				.where()
					.property("p", "company.name").eq("C2")
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertResultContains(p);
		assertNoMoreResults();
	}

	@Test
	public void singleSourceInConditionOnEntityProperty() {
		Company c1 = b.company("C1").create();
		Company c2 = b.company("C2").create();

		Person p;

		p = b.owner("P1").company(c1).create();
		p = b.owner("P2").company(c2).create();

		// @formatter:off
		SelectQuery selectQuery = query()
				.from(Person.T, "person")
				.where()
					.property("person", "company").in(asSet(c2))
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertResultContains(p);
		assertNoMoreResults();
	}

	@Test
	public void singleSourceInConditionOnListProperty() {
		Company c1 = b.company("C1").create();
		c1.setPersonNameList(Collections.singletonList("Pit"));

		// @formatter:off
		SelectQuery selectQuery = query()
				.from(Company.class, "c")
				.where()
					.value("Pit").in().property("c", "personNameList")
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertResultContains(c1);
		assertNoMoreResults();
	}

	@Test
	public void singleSourceInConditionOnSetProperty() {
		Company c1 = b.company("C1").create();
		c1.setPersonNameSet(Collections.singleton("Pit"));

		// @formatter:off
		SelectQuery selectQuery = query()
				.from(Company.class, "c")
				.where()
					.value("Pit").in().property("c", "personNameSet")
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertResultContains(c1);
		assertNoMoreResults();
	}

	/**
	 * Difference to previous test ({@link #singleSourceInConditionOnEntityProperty()}) is that this is using
	 * <tt>inEntities</tt> rather than just <tt>in</tt>, meaning the actual set will be turned into references
	 * (remotified).
	 */
	@Test
	public void singleSourceInConditionOnEntityProperty_Remote() {
		Company c1 = b.company("C1").create();
		Company c2 = b.company("C2").create();

		Person p;

		p = b.owner("P1").company(c1).create();
		p = b.owner("P2").company(c2).create();

		// @formatter:off
		SelectQuery selectQuery = query()
				.from(Person.T, "person")
				.where()
					.property("person", "company").inEntities(asSet(c2))
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertResultContains(p);
		assertNoMoreResults();
	}

	@Test
	public void singleSource_EnumCondition() {
		Person p;

		p = b.person("P1").eyeColor(Color.RED).create();
		p = b.person("P2").eyeColor(Color.GREEN).create();

		// @formatter:off
		SelectQuery selectQuery = query()
				.from(Person.T, "p")
				.where()
					.property("p", "eyeColor").eq(Color.GREEN)
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertResultContains(p);
		assertNoMoreResults();
	}

	@Test
	public void singleSource_EnumCondition_Remote() {
		Person p;

		p = b.person("P1").eyeColor(Color.RED).create();
		p = b.person("P2").eyeColor(Color.GREEN).create();

		EnumReference greenReference = typeReflection.<EnumType> getType(Color.class).getEnumReference(Color.GREEN);

		// @formatter:off
		SelectQuery selectQuery = query()
				.from(Person.T, "p")
				.where()
					.property("p", "eyeColor").eq(greenReference)
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertResultContains(p);
		assertNoMoreResults();
	}

	/** SeeBasicUseCaseTests#singleSourceNonIndexConditionOnJoined() */
	@Test
	public void singleSourceNonIndexConditionOnJoined() {
		Owner p;

		Company apple = b.company("Apple").create();
		Company hp = b.company("HP").create();

		p = b.owner("Steve Jobs").company(apple).create();
		p = b.owner("Bill Hewlett").company(hp).create();

		// @formatter:off
		SelectQuery selectQuery = query()
				.from(Owner.T, "person")
				.join("person", "company", "company")
				.where()
					.property("company", "name").eq("HP")
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertResultContains(p, hp);
		assertNoMoreResults();
	}

	/** SeeBasicUseCaseTests#singleSourceNonIndexConditionOnJoined() */
	@Test
	public void singleSourceNonIndexConditionOnFrom() {
		Person p;

		Company apple = b.company("Apple").create();
		Company hp = b.company("HP").create();

		p = b.owner("Steve Jobs").company(apple).create();
		p = b.owner("Bill Hewlett").company(hp).create();

		// @formatter:off
		SelectQuery selectQuery = query()
				.from(Owner.T, "person")
				.join("person", "company", "company")
				.where()
					.property("person", "name").eq("Bill Hewlett")
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertResultContains(p, hp);
		assertNoMoreResults();
	}

	/** SeeBasicUseCaseTests#singleSourceNonIndexConjunctionOfConditions() */
	@Test
	public void singleSourceNonIndexConjunctionOfConditions() {
		Person p;

		p = b.person("Jack").companyName("samsung").phoneNumber("555-45-96").create();
		p = b.person("John").companyName("toshiba").phoneNumber("555-45-96").create();
		p = b.person("John").companyName("samsung").phoneNumber("111-45-96").create();
		p = b.person("John").companyName("samsung").phoneNumber("555-45-96").create();

		// @formatter:off
		SelectQuery selectQuery = query()
				.from(Person.T, "person")
				.where()
					.conjunction()
						.property("person", "name").eq("John")
						.property("person", "companyName").ilike("S*")
						.property("person", "phoneNumber").like("555-*")
					.close()
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertResultContains(p);
		assertNoMoreResults();
	}

	/** SeeBasicUseCaseTests#singleSourceNonIndexConjunctionOfConditionsWithJoin() */
	@Test
	public void singleSourceNonIndexConjunctionOfConditionsWithJoin() {
		Company apple = b.company("Apple").create();
		Company hp = b.company("HP").create();

		Owner o;

		o = b.owner("Jack").company(hp).phoneNumber("555-45-96").create();
		o = b.owner("John").company(apple).phoneNumber("555-45-96").create();
		o = b.owner("John").company(hp).phoneNumber("111-45-96").create();
		o = b.owner("John").company(hp).phoneNumber("555-45-96").create();

		// @formatter:off
		SelectQuery selectQuery = query()
				.from(Owner.T, "person")
				.join("person", "company", "company")
				.where()
					.conjunction()
						.property("person", "name").eq("John")
						.property("person", "phoneNumber").like("555-*")
						.property("company", "name").eq("HP")
					.close()
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertResultContains(o, hp);
		assertNoMoreResults();
	}

	/** SeeBasicUseCaseTests#singleSourceNonIndexDisjunctionOfConditions() */
	@Test
	public void singleSourceNonIndexDisjunctionOfConditions() {
		Person p1 = b.person("Jack").companyName("samsung").phoneNumber("111-45-96").create();
		Person p2 = b.person("Jack").companyName("toshiba").phoneNumber("555-45-96").create();
		Person p3 = b.person("John").companyName("toshiba").phoneNumber("111-45-96").create();

		// @formatter:off
		SelectQuery selectQuery = query()
				.from(Person.T, "person")
				.where()
				.disjunction()
						.property("person", "name").eq("John")
						.property("person", "companyName").ilike("S*")
						.property("person", "phoneNumber").like("555-*")
				.close()
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertResultContains(p1);
		assertResultContains(p2);
		assertResultContains(p3);
		assertNoMoreResults();
	}

	// ####################################
	// ## . . . . collection joins . . . ##
	// ####################################

	/** SeeBasicUseCaseTests#joinWithSet() */
	@Test
	public void joinWithSet() {
		Company c1 = b.company("C1").create();
		Company c2 = b.company("C2").create();
		Company c3 = b.company("C3").create();

		Owner o = b.owner("John").addToCompanySet(c1, c2, c3).create();

		// @formatter:off
		SelectQuery selectQuery = query()
				.from(Owner.T, "person")
				.join("person", "companySet", "cs")
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertResultContains(o, c1);
		assertResultContains(o, c2);
		assertResultContains(o, c3);
		assertNoMoreResults();
	}

	/** SeeBasicUseCaseTests#joinWithListAndCondition() */
	@Test
	public void joinWithListAndCondition() {
		Company c1 = b.company("C1").create();
		Company c2 = b.company("C2").create();
		Company c3 = b.company("C3").create();

		Owner o = b.owner("John").addToCompanyList(c1, c2, c3).create();

		// @formatter:off
		SelectQuery selectQuery = query()
				.from(Owner.T, "person")
				.join("person", "companyList", "cs")
				.where()
					.listIndex("cs").le(1)
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertResultContains(o, c1);
		assertResultContains(o, c2);
		assertNoMoreResults();
	}

	/** SeeBasicUseCaseTests#joinWithMapAndCondition() */
	@Test
	public void joinWithMapAndCondition() {
		Company c1 = b.company("C1").create();
		Company c2 = b.company("C2").create();
		Company c3 = b.company("C3").create();

		Owner o = b.owner("John").addToCompanyMap("c1", c1).addToCompanyMap("c2", c2).addToCompanyMap("c3", c3).create();

		// @formatter:off
		SelectQuery selectQuery = query()
				.from(Owner.T, "person")
				.join("person", "companyMap", "cs")
				.where()
					.mapKey("cs").eq("c2")
				.done();
		// @formatter:on

		evaluate(selectQuery);

		assertResultContains(o, c2);
		assertNoMoreResults();
	}
}
