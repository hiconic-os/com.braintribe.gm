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
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.meta.GmModelElement;
import com.braintribe.model.meta.info.GmEntityTypeInfo;
import com.braintribe.model.meta.info.GmEnumConstantInfo;
import com.braintribe.model.meta.info.GmEnumTypeInfo;
import com.braintribe.model.meta.info.GmPropertyInfo;

/**
 * Base type for referencing an entity, property, enum or enum constant in a model.
 */
public interface QualifiedModelElement extends GenericEntity {

	EntityType<QualifiedModelElement> T = EntityTypes.T(QualifiedModelElement.class);

	GmModelElement getModelElement();
	void setModelElement(GmModelElement modelElement);

	GmModelElement getModelElementOwner();
	void setModelElementOwner(GmModelElement modelElementOwner);

	// Model

	default boolean targetsModel() {
		return getModelElement() == null;
	}

	// Entity

	default boolean targetsEntityType() {
		return getModelElement() instanceof GmEntityTypeInfo;
	}

	default GmEntityTypeInfo entityTypeInfo() {
		return (GmEntityTypeInfo) getModelElement();
	}

	default boolean targetsProperty() {
		return getModelElement() instanceof GmPropertyInfo;
	}

	default GmPropertyInfo propertyInfo() {
		return (GmPropertyInfo) getModelElement();
	}

	default GmEntityTypeInfo propertyOwner() {
		GmEntityTypeInfo owner = (GmEntityTypeInfo) getModelElementOwner();
		if (owner == null) {
			GmPropertyInfo modelElement = (GmPropertyInfo) getModelElement();
			owner = modelElement.declaringTypeInfo();
		}

		return owner;
	}

	// Enum

	default boolean targetsEnumType() {
		return getModelElement() instanceof GmEnumTypeInfo;
	}

	default GmEnumTypeInfo enumTypeInfo() {
		return (GmEnumTypeInfo) getModelElement();
	}

	default boolean targetsConstant() {
		return getModelElement() instanceof GmEnumConstantInfo;
	}

	default GmEnumConstantInfo constantInfo() {
		return (GmEnumConstantInfo) getModelElement();
	}

	default GmEnumTypeInfo constantOwner() {
		GmEnumTypeInfo owner = (GmEnumTypeInfo) getModelElementOwner();
		if (owner == null) {
			GmEnumConstantInfo modelElement = (GmEnumConstantInfo) getModelElement();
			owner = modelElement.declaringTypeInfo();
		}

		return owner;
	}

}
