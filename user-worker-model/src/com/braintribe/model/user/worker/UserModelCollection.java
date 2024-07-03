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
package com.braintribe.model.user.worker;

import java.util.List;
import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.user.Identity;
import com.braintribe.model.user.Role;

public interface UserModelCollection extends GenericEntity {

	final EntityType<UserModelCollection> T = EntityTypes.T(UserModelCollection.class);

	public static final String identities = "identities";
	public static final String roles = "roles";

	// **************************************************************************
	// Getter/Setter
	// **************************************************************************

	public List<Identity> getIdentities();
	public void setIdentities(List<Identity> identities);

	public Set<Role> getRoles();
	public void setRoles(Set<Role> roles);

}
