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
package com.braintribe.model.processing.misc.model;

import java.util.List;

import com.braintribe.model.common.IdentifiableEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.processing.misc.NamableEntity;

/**
 * A Dummy class used for all MPC testing
 * 
 */

public interface Person extends IdentifiableEntity, NamableEntity, Comparable<Person> {

	EntityType<Person> T = EntityTypes.T(Person.class);
	
	// @formatter:off
	List<String> getFavouriteColours();
	void setFavouriteColours(List<String> favouriteColours);

	Person getDescendant();
	void setDescendant(Person descendant);
	// @formatter:on

	public static class DefaultMethods {
		public static int compareTo(Person me, Object o) {
			return me.getName().compareTo(((Person)o).getName());
		}
	}
}
