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
package com.braintribe.testing.model.test.demo.person;

import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * A <code>Person</code> represents a real (human) person and therefore has a {@link #getLastName() name}, hopefully
 * some {@link #getFriends() friends}, etc.
 *
 * @author michael.lafite
 */

public interface Person extends GenericEntity {

	EntityType<Person> T = EntityTypes.T(Person.class);

	String getFirstName();
	void setFirstName(String value);

	String getLastName();
	void setLastName(String value);

	Integer getAge();
	void setAge(Integer age);

	Address getAddress();
	void setAddress(Address address);

	Set<Person> getFriends();
	void setFriends(Set<Person> friends);

	Person getMother();
	void setMother(Person person);

	Person getFather();
	void setFather(Person father);

	Set<Person> getChildren();
	void setChildren(Set<Person> children);
}
