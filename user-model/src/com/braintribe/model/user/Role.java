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

import com.braintribe.model.generic.StandardStringIdentifiable;
import com.braintribe.model.generic.annotation.SelectiveInformation;
import com.braintribe.model.generic.annotation.meta.Unique;
import com.braintribe.model.generic.i18n.LocalizedString;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * @author gunther.schenk
 *
 */
@SelectiveInformation("${name}")
public interface Role extends StandardStringIdentifiable, AuthEntity {

	EntityType<Role> T = EntityTypes.T(Role.class);
	
	String name = "name";
	String localizedName = "localizedName";
	String description = "description";
	
	@Override
	@Unique
	String getName();

	LocalizedString getLocalizedName();
	void setLocalizedName(LocalizedString localizedName);
	
	LocalizedString getDescription();
	void setDescription(LocalizedString description);

	@Override
	default String roleName() {
		return getName();
	}
	
}
