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
package com.braintribe.model.processing.query.test.builder;

import static com.braintribe.utils.lcd.CollectionTools2.asList;
import static com.braintribe.utils.lcd.CollectionTools2.asSet;

import java.util.Date;

import com.braintribe.model.processing.query.test.model.Address;
import com.braintribe.model.processing.query.test.model.Color;
import com.braintribe.model.processing.query.test.model.Company;
import com.braintribe.model.processing.query.test.model.Person;
import com.braintribe.model.processing.smood.Smood;

/**
 * 
 */
public class CompanyBuilder extends AbstractBuilder<Company, CompanyBuilder> {

	public static CompanyBuilder newCompany(DataBuilder dataBuilder) {
		return new CompanyBuilder(dataBuilder.smood);
	}

	public CompanyBuilder(Smood smood) {
		super(Company.class, smood);
	}

	public CompanyBuilder name(String value) {
		instance.setName(value);
		return this;
	}

	public CompanyBuilder description(String value) {
		instance.setDescription(value);
		return this;
	}
	
	public CompanyBuilder indexedName(String value) {
		instance.setIndexedName(value);
		return this;
	}

	public CompanyBuilder date(Date value) {
		instance.setIndexedDate(value);
		return this;
	}

	public CompanyBuilder address(Address value) {
		instance.setAddress(value);
		return this;
	}

	public CompanyBuilder owner(Person person) {
		instance.setOwner(person);
		return this;
	}

	public CompanyBuilder persons(Person... persons) {
		instance.setPersons(asList(persons));
		instance.setPersonSet(asSet(persons));
		return this;
	}

	public CompanyBuilder personNames(String... names) {
		instance.setPersonNameList(asList(names));
		instance.setPersonNameSet(asSet(names));
		return this;
	}

	public CompanyBuilder colors(Color... colors) {
		instance.setColors(asSet(colors));
		return this;
	}

}
