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
package com.braintribe.model.generic.typecondition.logic;

import java.util.List;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.typecondition.TypeCondition;
import com.braintribe.model.generic.typecondition.TypeConditionType;
import com.braintribe.model.meta.GmType;

public interface TypeConditionConjunction extends TypeConditionJunction {

	EntityType<TypeConditionConjunction> T = EntityTypes.T(TypeConditionConjunction.class);

	@Override
	default boolean matches(GenericModelType type) {
		List<TypeCondition> operands = getOperands();

		if (operands != null)
			for (TypeCondition operand : operands)
				if (!operand.matches(type))
					return false;

		return true;
	}

	@Override
	default boolean matches(GmType type) {
		List<TypeCondition> operands = getOperands();

		if (operands != null)
			for (TypeCondition operand : operands)
				if (!operand.matches(type))
					return false;

		return true;
	}

	@Override
	default TypeConditionType tcType() {
		return TypeConditionType.conjunction;
	}

}
