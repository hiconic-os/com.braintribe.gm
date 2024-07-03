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
package com.braintribe.model.generic.typecondition.param;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.MapType;
import com.braintribe.model.generic.reflection.TypeCode;
import com.braintribe.model.generic.typecondition.TypeCondition;
import com.braintribe.model.generic.typecondition.TypeConditionType;
import com.braintribe.model.meta.GmMapType;
import com.braintribe.model.meta.GmType;
import com.braintribe.model.meta.GmTypeKind;

public interface MapKeyCondition extends TypeParameterCondition {

	EntityType<MapKeyCondition> T = EntityTypes.T(MapKeyCondition.class);

	@Override
	default boolean matches(GenericModelType type) {
		if (type.getTypeCode() != TypeCode.mapType)
			return false;

		GenericModelType parameterType = ((MapType) type).getKeyType();

		TypeCondition condition = getCondition();
		return condition == null || condition.matches(parameterType);
	}

	@Override
	default boolean matches(GmType type) {
		if (type.typeKind() != GmTypeKind.MAP)
			return false;

		GmType parameterType = ((GmMapType) type).getKeyType();

		TypeCondition condition = getCondition();
		return condition == null || condition.matches(parameterType);
	}

	@Override
	default TypeConditionType tcType() {
		return TypeConditionType.mapKeyCondition;
	}

}
