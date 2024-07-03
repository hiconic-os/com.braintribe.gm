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
package com.braintribe.model.processing.session.test.data;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.braintribe.model.descriptive.HasName;
import com.braintribe.model.generic.StandardIdentifiable;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * 
 */
public interface Person extends HasName, StandardIdentifiable {

	EntityType<Person> T = EntityTypes.T(Person.class);

	Person getBestFriend();
	void setBestFriend(Person bestFriend);

	List<Integer> getNumbers();
	void setNumbers(List<Integer> numbers);

	Set<Person> getFriendSet();
	void setFriendSet(Set<Person> friendSet);

	List<Person> getFriendList();
	void setFriendList(List<Person> friendList);

	Map<String, Person> getFriendMap();
	void setFriendMap(Map<String, Person> friendMap);

	Object getObject();
	void setObject(Object object);

}
