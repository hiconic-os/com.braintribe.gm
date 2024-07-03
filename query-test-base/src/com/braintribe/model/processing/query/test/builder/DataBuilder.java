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

import com.braintribe.model.processing.smood.Smood;

/**
 * 
 */
public class DataBuilder {

	protected final Smood smood;

	public DataBuilder(Smood smood) {
		this.smood = smood;
	}

	public AddressBuilder address(String name) {
		return AddressBuilder.newAddress(this).name(name);
	}

	public PersonBuilder person(String name) {
		return PersonBuilder.newPerson(this).name(name);
	}

	public OwnerBuilder owner(String name) {
		return OwnerBuilder.newOwner(this).name(name);
	}

	public CompanyBuilder company(String name) {
		return CompanyBuilder.newCompany(this).name(name);
	}

}
