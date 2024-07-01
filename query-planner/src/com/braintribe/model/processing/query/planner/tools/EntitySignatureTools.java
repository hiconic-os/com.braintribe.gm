// ============================================================================
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
// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
// ============================================================================
package com.braintribe.model.processing.query.planner.tools;

import static com.braintribe.utils.lcd.CollectionTools2.isEmpty;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.processing.query.tools.SourceTypeResolver;
import com.braintribe.model.query.PropertyOperand;
import com.braintribe.model.query.Source;
import com.braintribe.model.query.functions.EntitySignature;

/**
 * 
 */
public class EntitySignatureTools {

	/**
	 * Returns a string corresponding to a signature represented by given {@link EntitySignature} as long as the
	 * signature is unique (i.e. given operand represents a final entity type - i.e. an entity type without sub-types).
	 * Otherwise, this method returns <tt>null</tt>.
	 */
	@SuppressWarnings("deprecation")
	public static String getStaticSignature(EntitySignature signatureFunction) {
		Object operand = signatureFunction.getOperand();

		EntityType<?> operandType = resolveOperandEntityType(operand);
		// TODO this should check if sub-type exists in the model, not this way
		if (operandType != null && isEmpty(operandType.getSubTypes()))
			return operandType.getTypeSignature();
		else
			return null;
	}

	private static EntityType<?> resolveOperandEntityType(Object operand) {
		if (operand instanceof Source) {
			return (EntityType<?>) SourceTypeResolver.resolveType((Source) operand);

		} else if (operand instanceof PropertyOperand) {
			GenericModelType propertyType = SourceTypeResolver.resolvePropertyType((PropertyOperand) operand, true);
			if (propertyType instanceof EntityType<?>)
				return (EntityType<?>) propertyType;
		}

		return null;
	}

}
