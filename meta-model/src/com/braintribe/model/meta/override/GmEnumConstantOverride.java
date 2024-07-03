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

import com.braintribe.model.generic.annotation.SelectiveInformation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.meta.GmEnumConstant;
import com.braintribe.model.meta.info.GmEnumConstantInfo;
import com.braintribe.model.meta.info.GmEnumTypeInfo;
import com.braintribe.model.weaving.override.ProtoGmEnumConstantOverride;

@SelectiveInformation(value = "${enumConstant.name} (override)")
public interface GmEnumConstantOverride extends ProtoGmEnumConstantOverride, GmEnumConstantInfo {

	EntityType<GmEnumConstantOverride> T = EntityTypes.T(GmEnumConstantOverride.class);

	@Override
	GmEnumConstant getEnumConstant();
	void setEnumConstant(GmEnumConstant enumConstant);

	@Override
	GmEnumTypeOverride getDeclaringTypeOverride();
	void setDeclaringTypeOverride(GmEnumTypeOverride declaringTypeOverride);

	@Override
	default GmEnumConstant relatedConstant() {
		return getEnumConstant();
	}

	@Override
	default GmEnumTypeInfo declaringTypeInfo() {
		return getDeclaringTypeOverride();
	}

}
