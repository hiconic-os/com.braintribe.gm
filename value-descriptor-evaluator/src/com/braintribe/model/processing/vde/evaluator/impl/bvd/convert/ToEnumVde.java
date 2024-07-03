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
package com.braintribe.model.processing.vde.evaluator.impl.bvd.convert;

import com.braintribe.model.bvd.convert.ToEnum;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.reflection.EnumType;
import com.braintribe.model.processing.vde.evaluator.api.ValueDescriptorEvaluator;
import com.braintribe.model.processing.vde.evaluator.api.VdeContext;
import com.braintribe.model.processing.vde.evaluator.api.VdeResult;
import com.braintribe.model.processing.vde.evaluator.api.VdeRuntimeException;
import com.braintribe.model.processing.vde.evaluator.impl.VdeResultImpl;

/**
 * {@link ValueDescriptorEvaluator} for {@link ToEnum}
 * 
 */
public class ToEnumVde implements ValueDescriptorEvaluator<ToEnum> {

	@Override
	public VdeResult evaluate(VdeContext context, ToEnum valueDescriptor) throws VdeRuntimeException {

		Object result = null;
		Object operand = context.evaluate(valueDescriptor.getOperand());
		
		EnumType enumType = GMF.getTypeReflection().getType(valueDescriptor.getTypeSignature());
		// operand is either a string or ordinal
		if (operand instanceof String) {
			result = enumType.getInstance((String) operand);
		} else if (operand instanceof Integer) {
			result = enumType.getEnumValues()[(Integer) operand];
		} else {
			throw new VdeRuntimeException("Operand must be of type String or Integer for ToEnum, not " + operand);
		}
		return new VdeResultImpl(result, false);
	}

}
