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

import static com.braintribe.utils.lcd.CollectionTools2.asList;

import org.junit.Assert;
import org.junit.Test;

import com.braintribe.model.bvd.context.UserName;
import com.braintribe.model.bvd.math.Subtract;
import com.braintribe.model.bvd.time.Now;
import com.braintribe.model.processing.query.fluent.EntityQueryBuilder;
import com.braintribe.model.processing.query.shortening.SmartShortening;
import com.braintribe.model.processing.query.test.model.Person;
import com.braintribe.model.query.EntityQuery;
import com.braintribe.model.query.Query;
import com.braintribe.model.query.conditions.ValueComparison;
import com.braintribe.model.time.TimeSpan;
import com.braintribe.model.time.TimeUnit;
import com.braintribe.testing.model.test.demo.person.Address;
/**
 *
 */
public class ValueDescriptorQueryTests extends AbstractSelectQueryTests {

	private final SmartShortening signatureExpert = new SmartShortening(getModelOracle());
	
	@Test
	public void userNameComparison() {
		EntityQuery q = EntityQueryBuilder
				.from(Person.T)
				.where()
				.property(Person.id).eq(null)// Will be set to username function afterwards (that's currently not supported by the querybuilder.
				.done();

		updateValueComparison(q, userName());

		String queryString = stringify(q);
		Assert.assertTrue(queryString.equalsIgnoreCase(
				"from com.braintribe.model.processing.query.test.model.Person where id = userName()"));
	}
	
	@Test
	public void lastDayComparison() {
		EntityQuery q =  EntityQueryBuilder
				.from(Person.T)
				.where()
					.property("birthDate").gt(null) // Will be set to `subtract(now(), timespan(1, TimeUnit.day))` function afterwards (that's currently not supported by the querybuilder).
				.done();

		Subtract lastDay = lastDay();
		TimeSpan timeSpan = (TimeSpan)lastDay.getOperands().get(1);
		
		updateValueComparison(q, lastDay);
		String queryString = stringify(q);
		System.out.println(queryString);
		Assert.assertTrue(queryString.equals(
				"from com.braintribe.model.processing.query.test.model.Person where birthDate > subtract(now(), reference(com.braintribe.model.time.TimeSpan, "+timeSpan.reference().getRefId()+"l, false))"));
		
	}

	@Test
	public void lastDayComparisonShortening() {
		EntityQuery q =  EntityQueryBuilder
				.from(Person.T)
				.where()
					.property("birthDate").gt(null) // Will be set to `subtract(now(), timespan(1, TimeUnit.day))` function afterwards (that's currently not supported by the querybuilder).
				.done();

		Subtract lastDay = lastDay();
		TimeSpan timeSpan = (TimeSpan)lastDay.getOperands().get(1);
		
		updateValueComparison(q, lastDay);
		String queryString = stringify(q, signatureExpert);
		System.out.println(queryString);
		Assert.assertTrue(queryString.equals(
				"from Person where birthDate > subtract(now(), reference(com.braintribe.model.time.TimeSpan, "+timeSpan.reference().getRefId()+"l, false))"));
		
	}

	@Test
	public void subQueryComparison() {
		
		EntityQuery aq = EntityQueryBuilder
				.from(Address.T)
				.where()
					.property("street").eq("Kandlgasse")
				.done();
		
		EntityQuery pq =  EntityQueryBuilder
				.from(Person.T)
				.where()
					.property("address").in().value(query(aq))
				.done();

		
		
		String queryString = stringify(pq, signatureExpert);
		System.out.println(queryString);
		Assert.assertTrue(queryString.equals(
				"from Person where address in query(from Address where street = 'Kandlgasse', enum(com.braintribe.model.bvd.query.ResultConvenience, list))"));
		
	}
	
	private com.braintribe.model.bvd.query.Query query(Query query) {
		com.braintribe.model.bvd.query.Query queryVd = com.braintribe.model.bvd.query.Query.T.create();
		queryVd.setQuery(query);
		return queryVd;
	}
	
	
	private void updateValueComparison(Query q, Object rightOperand) {
		ValueComparison comparison = (ValueComparison) q.getRestriction().getCondition();
		comparison.setRightOperand(rightOperand);
	}

	private UserName userName() {
		return UserName.T.create();
	}

	private Now now() {
		return Now.T.create();
	}

	private Subtract lastDay() {
		Subtract lastDayFunction = Subtract.T.create();
		lastDayFunction.setOperands(asList(now(), oneDayTimeSpan()));
		return lastDayFunction;
	}

	private TimeSpan oneDayTimeSpan() {
		TimeSpan oneDay = TimeSpan.T.create();
		oneDay.setValue(1d);
		oneDay.setUnit(TimeUnit.day);
		return oneDay;
	}

}
