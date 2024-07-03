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
package com.braintribe.model.user;

import java.util.Date;
import java.util.Set;

import com.braintribe.model.descriptive.HasPassword;
import com.braintribe.model.generic.annotation.meta.Unique;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * @author gunther.schenk
 */

public interface User extends Identity, HasPassword {

	final EntityType<User> T = EntityTypes.T(User.class);

	public static final String firstName = "firstName";
	public static final String lastName = "lastName";
	public static final String groups = "groups";
	public static final String lastLogin = "lastLogin";
	public static final String password = "password";

	// **************************************************************************
	// Getter/Setter
	// **************************************************************************
	
	@Override
	@Unique
	String getName();

	public String getFirstName();
	public void setFirstName(String firstName);

	public String getLastName();
	public void setLastName(String lastName);
	
	public void setGroups(Set<Group> groups);
	public Set<Group> getGroups();
	
	public Date getLastLogin();
	public void setLastLogin(Date lastLogin);

	@Override
	default String roleName() {
		return "$user-"+getName();
	}
}
