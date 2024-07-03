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
package com.braintribe.model.meta;

import java.util.List;

import com.braintribe.model.generic.annotation.SelectiveInformation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.meta.info.GmEntityTypeInfo;
import com.braintribe.model.weaving.ProtoGmEntityType;

@SelectiveInformation(value = "${typeSignature}")
public interface GmEntityType extends ProtoGmEntityType, GmCustomType, GmEntityTypeInfo {

	EntityType<GmEntityType> T = EntityTypes.T(GmEntityType.class);

	@Override
	boolean getIsAbstract();
	@Override
	void setIsAbstract(boolean isAbstract);

	@Override
	List<GmEntityType> getSuperTypes();
	void setSuperTypes(List<GmEntityType> superTypes);

	@Override
	List<GmProperty> getProperties();
	void setProperties(List<GmProperty> properties);

	@Override
	GmType getEvaluatesTo();
	void setEvaluatesTo(GmType evaluatesTo);

	@Override
	default GmTypeKind typeKind() {
		return GmTypeKind.ENTITY;
	}

	@Override
	default boolean isGmEntity() {
		return true;
	}

	@Override
	default GmEntityType addressedType() {
		return this;
	}

}
