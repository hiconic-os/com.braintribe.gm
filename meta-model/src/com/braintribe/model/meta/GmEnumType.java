// ============================================================================
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
// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
// ============================================================================
package com.braintribe.model.meta;

import java.util.List;

import com.braintribe.model.generic.annotation.SelectiveInformation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.meta.info.GmEnumTypeInfo;
import com.braintribe.model.weaving.ProtoGmEnumType;

@SelectiveInformation(value = "${typeSignature}")
public interface GmEnumType extends ProtoGmEnumType, GmCustomType, GmEnumTypeInfo, GmScalarType {

	EntityType<GmEnumType> T = EntityTypes.T(GmEnumType.class);

	@Override
	List<GmEnumConstant> getConstants();
	void setConstants(List<GmEnumConstant> constants);

	@Override
	default GmTypeKind typeKind() {
		return GmTypeKind.ENUM;
	}

	@Override
	default boolean isGmEnum() {
		return true;
	}

	@Override
	default GmEnumType addressedType() {
		return this;
	}
	
	@Override
	default GmMetaModel declaringModel() {
		return getDeclaringModel();
	}

}
