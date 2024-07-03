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

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.typecondition.TypeCondition;
import com.braintribe.model.generic.typecondition.TypeConditionType;
import com.braintribe.model.meta.GmType;

public interface TypeConditionNegation extends TypeConditionLogicalOperation {

	EntityType<TypeConditionNegation> T = EntityTypes.T(TypeConditionNegation.class);

	TypeCondition getOperand();
	void setOperand(TypeCondition operand);

	@Override
	default boolean matches(GenericModelType type) {
		TypeCondition operand = getOperand();
		return operand != null && !operand.matches(type);
	}

	@Override
	default boolean matches(GmType type) {
		TypeCondition operand = getOperand();
		return operand != null && !operand.matches(type);
	}

	@Override
	default TypeConditionType tcType() {
		return TypeConditionType.negation;
	}

}
