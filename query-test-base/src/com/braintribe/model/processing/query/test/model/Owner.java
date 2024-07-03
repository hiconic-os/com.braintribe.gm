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
package com.braintribe.model.processing.query.test.model;

import java.util.List;
import java.util.Map;
import java.util.Set;


import com.braintribe.model.generic.annotation.ToStringInformation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

@ToStringInformation("Owner[$;]")
public interface Owner extends Person {

	EntityType<Owner> T = EntityTypes.T(Owner.class);

	Set<Company> getCompanySet();
	void setCompanySet(Set<Company> companySet);

	List<Company> getCompanyList();
	void setCompanyList(List<Company> companyList);

	Map<String, Company> getCompanyMap();
	void setCompanyMap(Map<String, Company> companyMap);

	Map<String, String> getCompanyTypeMap();
	void setCompanyTypeMap(Map<String, String> companyTypeMap);

	Map<Company, Integer> getCompanyValueMap();
	void setCompanyValueMap(Map<Company, Integer> companyValueMap);

}
