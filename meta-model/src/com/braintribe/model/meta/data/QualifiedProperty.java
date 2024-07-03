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
package com.braintribe.model.meta.data;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.ToStringInformation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.meta.GmEntityType;
import com.braintribe.model.meta.GmProperty;
import com.braintribe.model.meta.GmType;

/**
 * This exists to enable referencing of properties of given {@link GmEntityType} which are inherited from a super-type.
 */
@ToStringInformation("${entityType.typeSignature}.${property.name}")
public interface QualifiedProperty extends GenericEntity {

	EntityType<QualifiedProperty> T = EntityTypes.T(QualifiedProperty.class);

	/**
	 * The entityType must be either a subType of {@link #getProperty() property}'s declaring entity type, or
	 * <code>null</code>, in which case we consider it to be the property's declaring type.
	 */
	GmEntityType getEntityType();
	void setEntityType(GmEntityType entityType);

	GmProperty getProperty();
	void setProperty(GmProperty property);

	default GmEntityType propertyOwner() {
		GmEntityType owner = getEntityType();
		return owner == null ? getProperty().getDeclaringType() : owner;
	}

	default String propertyName() {
		return getProperty().getName();
	}

	default GmType propertyType() {
		return getProperty().getType();
	}

}
