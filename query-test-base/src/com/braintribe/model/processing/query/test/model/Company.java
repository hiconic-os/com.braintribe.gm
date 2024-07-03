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

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.braintribe.model.generic.annotation.ToStringInformation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

@ToStringInformation("Company[${name}]")
public interface Company extends NamedEntity {

	EntityType<Company> T = EntityTypes.T(Company.class);

	String getIndexedName();
	void setIndexedName(String indexedName);

	String getDescription();
	void setDescription(String description);

	Date getIndexedDate();
	void setIndexedDate(Date indexedDate);

	Person getOwner();
	void setOwner(Person owner);

	List<Person> getPersons();
	void setPersons(List<Person> persons);

	Set<Person> getPersonSet();
	void setPersonSet(Set<Person> personSet);

	List<String> getPersonNameList();
	void setPersonNameList(List<String> personNameList);

	Set<String> getPersonNameSet();
	void setPersonNameSet(Set<String> personNameSet);

	Address getAddress();
	void setAddress(Address address);

	Set<Address> getAddressSet();
	void setAddressSet(Set<Address> addressSet);

	List<Address> getAddressList();
	void setAddressList(List<Address> addressList);

	Map<String, Address> getAddressMap();
	void setAddressMap(Map<String, Address> addressMap);

	Set<Color> getColors();
	void setColors(Set<Color> colors);

}
