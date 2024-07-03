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

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.annotation.SelectiveInformation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.weaving.ProtoGmSetType;

@SelectiveInformation(value = "${typeSignature}")
public interface GmSetType extends ProtoGmSetType, GmLinearCollectionType {

	EntityType<GmSetType> T = EntityTypes.T(GmSetType.class);

	@Override
	default GmTypeKind typeKind() {
		return GmTypeKind.SET;
	}

	@Override
	default GenericModelType reflectionType() {
		return GMF.getTypeReflection().getSetType(getElementType().reflectionType());
	}

}
