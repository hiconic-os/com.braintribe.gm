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
package com.braintribe.model.generic.typecondition;

import java.util.Arrays;

import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.typecondition.basic.IsAnyType;
import com.braintribe.model.generic.typecondition.basic.IsAssignableTo;
import com.braintribe.model.generic.typecondition.basic.IsType;
import com.braintribe.model.generic.typecondition.basic.IsTypeKind;
import com.braintribe.model.generic.typecondition.basic.TypeKind;
import com.braintribe.model.generic.typecondition.logic.TypeConditionConjunction;
import com.braintribe.model.generic.typecondition.logic.TypeConditionDisjunction;
import com.braintribe.model.generic.typecondition.logic.TypeConditionNegation;
import com.braintribe.model.generic.typecondition.origin.IsDeclaredIn;
import com.braintribe.model.generic.typecondition.origin.IsRelatedToTypeDeclaredIn;
import com.braintribe.model.generic.typecondition.param.CollectionElementCondition;
import com.braintribe.model.generic.typecondition.param.MapKeyCondition;
import com.braintribe.model.generic.typecondition.param.MapValueCondition;

public interface TypeConditions {

	/** same as {@link #or} for namespace clashes */
	static TypeConditionDisjunction orTc(TypeCondition... operands) {
		return or(operands);
	}

	static TypeConditionDisjunction or(TypeCondition... operands) {
		TypeConditionDisjunction result = TypeConditionDisjunction.T.create();
		result.getOperands().addAll(Arrays.asList(operands));
		return result;
	}

	/** same as {@link #and} for namespace clashes */
	static TypeConditionConjunction andTc(TypeCondition... operands) {
		return and(operands);
	}

	static TypeConditionConjunction and(TypeCondition... operands) {
		TypeConditionConjunction result = TypeConditionConjunction.T.create();
		result.getOperands().addAll(Arrays.asList(operands));
		return result;
	}

	/** same as {@link #not} for namespace clashes */
	static TypeConditionNegation notTc(TypeCondition operand) {
		return not(operand);
	}

	static TypeConditionNegation not(TypeCondition operand) {
		TypeConditionNegation result = TypeConditionNegation.T.create();
		result.setOperand(operand);
		return result;
	}

	static IsTypeKind isKind(TypeKind kind) {
		IsTypeKind result = IsTypeKind.T.create();
		result.setKind(kind);
		return result;
	}

	static IsType isType(String typeSignature) {
		IsType result = IsType.T.create();
		result.setTypeSignature(typeSignature);
		return result;
	}

	static IsType isType(GenericModelType type) {
		return isType(type.getTypeSignature());
	}

	static IsAssignableTo isAssignableTo(String typeSignature) {
		IsAssignableTo result = IsAssignableTo.T.create();
		result.setTypeSignature(typeSignature);
		return result;
	}

	static IsAssignableTo isAssignableTo(GenericModelType type) {
		return isAssignableTo(type.getTypeSignature());
	}

	static IsAnyType isAny() {
		return IsAnyType.T.create();
	}

	static MapKeyCondition hasMapKey(TypeCondition operand) {
		MapKeyCondition result = MapKeyCondition.T.create();
		result.setCondition(operand);
		return result;
	}

	static MapValueCondition hasMapValue(TypeCondition operand) {
		MapValueCondition result = MapValueCondition.T.create();
		result.setCondition(operand);
		return result;
	}

	static CollectionElementCondition hasCollectionElement(TypeCondition operand) {
		CollectionElementCondition result = CollectionElementCondition.T.create();
		result.setCondition(operand);
		return result;
	}

	static IsDeclaredIn isDeclaredIn(String modelName) {
		IsDeclaredIn result = IsDeclaredIn.T.create();
		result.setModelName(modelName);
		return result;
	}

	static IsRelatedToTypeDeclaredIn isRelatedToTypeDeclaredIn(String modelName) {
		IsRelatedToTypeDeclaredIn result = IsRelatedToTypeDeclaredIn.T.create();
		result.setModelName(modelName);
		return result;
	}

}
