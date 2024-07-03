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
package com.braintribe.model.processing.security.manipulation;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.ToStringInformation;
import com.braintribe.model.generic.manipulation.AtomicManipulation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.value.EntityReference;

/**
 * An entry that describes some security violation reported by a {@link ManipulationSecurityExpert}
 */
@ToStringInformation("${description}")
public interface SecurityViolationEntry extends GenericEntity {
	
	final EntityType<SecurityViolationEntry> T = EntityTypes.T(SecurityViolationEntry.class);

	// @formatter:off
	AtomicManipulation getCausingManipulation();
	void setCausingManipulation(AtomicManipulation causingManipulation);

	EntityReference getEntityReference();
	void setEntityReference(EntityReference entityReference);

	String getPropertyName();
	void setPropertyName(String propertyName);

	String getDescription();
	void setDescription(String description);
	// @formatter:on

}
