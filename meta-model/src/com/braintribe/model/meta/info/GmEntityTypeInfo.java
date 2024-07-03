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

import java.util.List;
import java.util.Set;

import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.annotation.TypeRestriction;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.meta.GmEntityType;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.meta.GmProperty;
import com.braintribe.model.meta.data.EntityTypeMetaData;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.meta.data.PropertyMetaData;
import com.braintribe.model.meta.data.UniversalMetaData;
import com.braintribe.model.meta.override.GmPropertyOverride;
import com.braintribe.model.weaving.info.ProtoGmEntityTypeInfo;

@Abstract
public interface GmEntityTypeInfo extends ProtoGmEntityTypeInfo, GmCustomTypeInfo {

	EntityType<GmEntityTypeInfo> T = EntityTypes.T(GmEntityTypeInfo.class);

	@Override
	List<GmPropertyOverride> getPropertyOverrides();
	void setPropertyOverrides(List<GmPropertyOverride> propertyOverrides);

	@Override
	@TypeRestriction({ EntityTypeMetaData.class, UniversalMetaData.class })
	Set<MetaData> getMetaData();

	/**
	 * {@link MetaData} which are valid for all the {@link GmProperty}s of this entity type.
	 * 
	 * NOTE: We use this extra property rather than having all the entity and property MD in one collection (possibly
	 * with a selector or a wrapper to specify the target) because it's easier to do for now (since we used it till now)
	 * and we might change this alter.
	 */
	@TypeRestriction({ PropertyMetaData.class, UniversalMetaData.class })
	Set<MetaData> getPropertyMetaData();
	void setPropertyMetaData(Set<MetaData> propertyMetaData);

	@Override
	GmEntityType addressedType();

	@Override
	default String nameWithOrigin() {
		GmMetaModel declaringModel = getDeclaringModel();
		String origin = declaringModel == null ? " (free)" : " of " + declaringModel.getName();

		return "GE:" + toSelectiveInformation() + origin;
	}

}
