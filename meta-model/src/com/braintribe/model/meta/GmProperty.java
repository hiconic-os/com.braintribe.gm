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

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.Initializer;
import com.braintribe.model.generic.annotation.SelectiveInformation;
import com.braintribe.model.generic.annotation.ToStringInformation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.meta.info.GmEntityTypeInfo;
import com.braintribe.model.meta.info.GmPropertyInfo;
import com.braintribe.model.meta.restriction.GmTypeRestriction;
import com.braintribe.model.weaving.ProtoGmProperty;

@SelectiveInformation("${name}")
@ToStringInformation("${declaringType.typeSignature}.${name}")
public interface GmProperty extends ProtoGmProperty, GmPropertyInfo {

	EntityType<GmProperty> T = EntityTypes.T(GmProperty.class);

	@Override
	String getName();
	@Override
	void setName(String name);

	@Override
	GmType getType();
	void setType(GmType type);

	@Override
	GmEntityType getDeclaringType();
	void setDeclaringType(GmEntityType declaringType);

	@Override
	GmTypeRestriction getTypeRestriction();
	void setTypeRestriction(GmTypeRestriction typeRestriction);

	@Override
	@Initializer("true")
	boolean getNullable();
	@Override
	void setNullable(boolean nullable);

	default boolean isId() {
		return GenericEntity.id.equals(getName());
	}

	@Override
	default GmProperty relatedProperty() {
		return this;
	}

	@Override
	default GmEntityTypeInfo declaringTypeInfo() {
		return getDeclaringType();
	}

}
