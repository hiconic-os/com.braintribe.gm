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
package com.braintribe.model.processing.query.eval.set.base;

import java.util.Date;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.GenericModelTypeReflection;
import com.braintribe.model.processing.query.test.model.Address;
import com.braintribe.model.processing.query.test.model.Company;
import com.braintribe.model.processing.query.test.model.Owner;
import com.braintribe.model.processing.query.test.model.Person;

/**
 * 
 */
public class ModelBuilder {

	private static final long MILLISECOND = 1;
	private static final long SECOND = 1000 * MILLISECOND;
	private static final long HOUR = 60 * 60 * SECOND;
	private static final long DAY = 24 * HOUR;
	private static final long YEAR = 365 * DAY;

	public static Owner owner(String name) {
		Owner result = instantiate(Owner.class);
		result.setName(name);

		return result;
	}

	public static Person person(String name) {
		return person(name, null, 0);
	}

	public static Person person(String name, String company, int age) {
		return fillPerson(instantiate(Person.class), name, company, age, new Date(System.currentTimeMillis() - age * YEAR));
	}

	public static Person person(String name, String company, int age, Date birthDate) {
		return fillPerson(instantiate(Person.class), name, company, age, birthDate);
	}

	public static Person fillPerson(Person result, String name) {
		return fillPerson(result, name, null, 0, null);
	}

	public static Person fillPerson(Person result, String name, String company, int age, Date birthDate) {
		result.setName(name);
		result.setIndexedName(name);
		result.setCompanyName(company);
		result.setAge(age);
		result.setBirthDate(birthDate);

		return result;
	}

	public static Company company(String name) {
		Company result = instantiate(Company.class);
		result.setName(name);
		result.setIndexedName(name);

		return result;
	}

	public static Address address(String street) {
		Address result = instantiate(Address.class);
		result.setStreet(street);

		return result;
	}

	protected static <T extends GenericEntity> T instantiate(Class<T> beanClass) {
		return typeReflection().<T> getEntityType(beanClass).create();
	}

	private static GenericModelTypeReflection typeReflection() {
		return GMF.getTypeReflection();
	}
}
