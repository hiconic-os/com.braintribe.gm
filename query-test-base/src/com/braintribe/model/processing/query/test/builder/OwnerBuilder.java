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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.braintribe.model.processing.query.test.model.Company;
import com.braintribe.model.processing.query.test.model.Owner;
import com.braintribe.model.processing.smood.Smood;

/**
 * 
 */
public class OwnerBuilder extends AbstractPersonBuilder<Owner, OwnerBuilder> {

	public static OwnerBuilder newOwner() {
		return new OwnerBuilder(null);
	}

	public static OwnerBuilder newOwner(DataBuilder dataBuilder) {
		return new OwnerBuilder(dataBuilder.smood);
	}

	public OwnerBuilder(Smood smood) {
		super(Owner.class, smood);
	}

	public OwnerBuilder addToCompanySet(Company... company) {
		Set<Company> cs = instance.getCompanySet();
		cs.addAll(Arrays.asList(company));

		return this;
	}

	public OwnerBuilder addToCompanyList(Company... company) {
		List<Company> cs = instance.getCompanyList();
		cs.addAll(Arrays.asList(company));

		return this;
	}

	public OwnerBuilder addToCompanyMap(String s, Company company) {
		Map<String, Company> map = instance.getCompanyMap();
		map.put(s, company);

		return this;
	}

	public OwnerBuilder addToCompanyTypeMap(String s, String type) {
		Map<String, String> map = instance.getCompanyTypeMap();
		map.put(s, type);

		return this;
	}

	public OwnerBuilder addToCompanyValueMap(Company company, Integer value) {
		Map<Company, Integer> map = instance.getCompanyValueMap();
		map.put(company, value);

		return this;
	}

}
