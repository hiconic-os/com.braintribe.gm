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
package com.braintribe.model.meta.override;

import java.util.List;

import com.braintribe.model.generic.annotation.SelectiveInformation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.meta.GmEnumType;
import com.braintribe.model.meta.info.GmEnumTypeInfo;
import com.braintribe.model.weaving.override.ProtoGmEnumTypeOverride;

@SelectiveInformation(value = "${enumType.typeSignature} (override)")
public interface GmEnumTypeOverride extends ProtoGmEnumTypeOverride, GmEnumTypeInfo, GmCustomTypeOverride {

	EntityType<GmEnumTypeOverride> T = EntityTypes.T(GmEnumTypeOverride.class);

	@Override
	GmEnumType getEnumType();
	void setEnumType(GmEnumType enumType);

	@Override
	List<GmEnumConstantOverride> getConstantOverrides();
	void setConstantOverrides(List<GmEnumConstantOverride> constantOverrides);

	@Override
	default boolean isGmEnumOverride() {
		return true;
	}

	@Override
	default GmEnumType addressedType() {
		return getEnumType();
	}

}
