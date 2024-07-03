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
import java.util.Set;

import com.braintribe.model.generic.annotation.ToStringInformation;
import com.braintribe.model.generic.i18n.LocalizedString;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

@ToStringInformation("Person[${name}]")
public interface Person extends NamedEntity {

	EntityType<Person> T = EntityTypes.T(Person.class);

	String getIndexedName();
	void setIndexedName(String indexedName);

	String getIndexedUniqueName();
	void setIndexedUniqueName(String indexedUniqueName);

	int getIndexedInteger();
	void setIndexedInteger(int indexedInteger);

	Company getCompany();
	void setCompany(Company company);

	Company getIndexedCompany();
	void setIndexedCompany(Company indexedCompany);

	String getCompanyName();
	void setCompanyName(String companyName);

	String getPhoneNumber();
	void setPhoneNumber(String phoneNumber);

	LocalizedString getLocalizedString();
	void setLocalizedString(LocalizedString localizedString);

	int getAge();
	void setAge(int age);

	Date getBirthDate();
	void setBirthDate(Date birthDate);

	Set<String> getNicknames();
	void setNicknames(Set<String> nicknames);

	Person getIndexedFriend();
	void setIndexedFriend(Person indexedFriend);

	Color getEyeColor();
	void setEyeColor(Color eyeColor);

	Set<Address> getAddressSet();
	void setAddressSet(Set<Address> addressSet);
	
}
