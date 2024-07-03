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
package com.braintribe.model.processing.cmd.test.model;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface Person extends GenericEntity {

	EntityType<Person> T = EntityTypes.T(Person.class);

	long getLongValue();
	void setLongValue(long longValue);

	String getName();
	void setName(String name);

	int getAge();
	void setAge(int age);

	Person getFriend();
	void setFriend(Person friend);

	List<Person> getFriends();
	void setFriends(List<Person> friends);

	Color getColor();
	void setColor(Color color);

	boolean getIsAlive();
	void setIsAlive(boolean b);

	Date getBirthDate();
	void setBirthDate(Date d);

	/* This property will be removed from properties of the sub-type (Teacher), and the MD still should be resolved */
	long getNotRepeatedProperty();
	void setNotRepeatedProperty(long b);
	
	
	Set<Person> getOtherFriends();
	void setOtherFriends(Set<Person> otherFriends);
	
	Map<String,String> getProperties();
	void setProperties(Map<String,String> properties);

}
