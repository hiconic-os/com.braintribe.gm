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
import com.braintribe.model.meta.GmCustomModelElement;
import com.braintribe.model.meta.GmEnumConstant;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.meta.data.EnumConstantMetaData;
import com.braintribe.model.meta.data.HasMetaData;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.meta.data.UniversalMetaData;
import com.braintribe.model.weaving.info.ProtoGmEnumConstantInfo;

@Abstract
public interface GmEnumConstantInfo extends ProtoGmEnumConstantInfo, HasMetaData, GmCustomModelElement {

	EntityType<GmEnumConstantInfo> T = EntityTypes.T(GmEnumConstantInfo.class);

	@Override
	@TypeRestriction({ EnumConstantMetaData.class, UniversalMetaData.class })
	Set<MetaData> getMetaData();

	@Override
	GmEnumConstant relatedConstant();

	@Override
	GmEnumTypeInfo declaringTypeInfo();

	@Override
	default GmMetaModel declaringModel() {
		return declaringTypeInfo().getDeclaringModel();
	}

	@Override
	default String nameWithOrigin() {
		GmEnumTypeInfo declaringTypeInfo = declaringTypeInfo();
		String origin = declaringTypeInfo == null ? " (free)" : " of " + declaringTypeInfo.nameWithOrigin();
		return "constant:" + toSelectiveInformation() + origin;
	}

}
