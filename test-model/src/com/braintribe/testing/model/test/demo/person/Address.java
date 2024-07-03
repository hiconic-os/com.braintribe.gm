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

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * Represents an address where a {@link Person} lives.
 *
 * @author michael.lafite
 */

public interface Address extends GenericEntity {

	EntityType<Address> T = EntityTypes.T(Address.class);

	String getCity();
	void setCity(String city);

	String getStreet();
	void setStreet(String street);

	/**
	 * For sake of simplicity this is an <code>Integer</code> (although there might be house numbers such as
	 * <code>21b</code>).
	 */
	Integer getHouseNumber();
	void setHouseNumber(Integer houseNumber);
}
