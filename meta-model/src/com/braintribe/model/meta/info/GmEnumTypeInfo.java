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
package com.braintribe.model.meta.info;

import java.util.Set;

import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.annotation.TypeRestriction;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.meta.GmEnumConstant;
import com.braintribe.model.meta.GmEnumType;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.meta.data.EnumConstantMetaData;
import com.braintribe.model.meta.data.EnumTypeMetaData;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.meta.data.UniversalMetaData;
import com.braintribe.model.weaving.info.ProtoGmEnumTypeInfo;

@Abstract
public interface GmEnumTypeInfo extends ProtoGmEnumTypeInfo, GmCustomTypeInfo {

	EntityType<GmEnumTypeInfo> T = EntityTypes.T(GmEnumTypeInfo.class);

	@Override
	@TypeRestriction({ EnumTypeMetaData.class, UniversalMetaData.class })
	Set<MetaData> getMetaData();

	/**
	 * Gets {@link EnumConstantMetaData} which are valid for all the {@link GmEnumConstant}s of this enum type.
	 *
	 * For reason why we keep this property see remark for {@link GmEntityTypeInfo#getPropertyMetaData()}
	 */
	@Override
	@TypeRestriction({ EnumConstantMetaData.class, UniversalMetaData.class })
	Set<MetaData> getEnumConstantMetaData();
	@Override
	void setEnumConstantMetaData(Set<MetaData> enumConstantMetaData);

	@Override
	GmEnumType addressedType();

	@Override
	default String nameWithOrigin() {
		GmMetaModel declaringModel = getDeclaringModel();
		String origin = declaringModel == null ? " (free)" : " of " + declaringModel.getName();
		return "Enum:" + toSelectiveInformation() + origin;
	}

}
