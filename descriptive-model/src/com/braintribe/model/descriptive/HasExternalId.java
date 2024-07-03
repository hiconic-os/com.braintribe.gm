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
package com.braintribe.model.descriptive;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.annotation.SelectiveInformation;
import com.braintribe.model.generic.annotation.meta.Mandatory;
import com.braintribe.model.generic.annotation.meta.Unique;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

@SelectiveInformation("${externalId}")
@Abstract
public interface HasExternalId extends GenericEntity {

	EntityType<HasExternalId> T = EntityTypes.T(HasExternalId.class);

	String externalId = "externalId";

	@Mandatory
	@Unique
	String getExternalId();
	void setExternalId(String externalId);

	/**
	 * Prints a simple, user-friendly descriptor of this instance, consisting at least of short type and externalId information, for example:
	 * {@code HibernateAccess[custom.access.id]}.
	 */
	default String simpleIdentification() {
		return entityType().getShortName() + "[" + getExternalId() + "]";
	}

}
