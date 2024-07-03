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
package com.braintribe.model.processing.validation.expert;

import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.typecondition.TypeCondition;
import com.braintribe.model.generic.typecondition.basic.IsAssignableTo;
import com.braintribe.model.processing.ValidationContext;
import com.braintribe.model.processing.ValidationExpert;
import com.braintribe.model.processing.traversing.api.path.TraversingModelPathElement;

/**
 * Validates the type of the currently visited {@link TraversingModelPathElement} against meeting a certain
 * {@link TypeCondition}
 * 
 * @author Neidhart.Orlich
 *
 */
public class TypeExpert implements ValidationExpert {
	private final TypeCondition typeCondition;

	public TypeExpert(TypeCondition typeCondition) {
		this.typeCondition = typeCondition;
	}

	@Override
	public void validate(ValidationContext context) {
		GenericModelType type = context.getPathElement().getType();
		if (!typeCondition.matches(type)) {
			String errorMessage;
			if (typeCondition instanceof IsAssignableTo) {
				IsAssignableTo isType = (IsAssignableTo) typeCondition;
				errorMessage = "Root type mismatch: Expected: type " + isType.getTypeSignature() + " but got type " + type.getTypeSignature();
			} else {
				errorMessage = "Root type (" + type.getTypeSignature() + ") did not match type condition: " + typeCondition;
			}

			context.notifyConstraintViolation(errorMessage);
		}
	}

}
