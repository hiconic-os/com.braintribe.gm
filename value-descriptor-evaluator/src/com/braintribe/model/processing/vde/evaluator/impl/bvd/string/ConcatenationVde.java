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
package com.braintribe.model.processing.vde.evaluator.impl.bvd.string;

import java.util.List;

import com.braintribe.model.bvd.string.Concatenation;
import com.braintribe.model.processing.vde.evaluator.api.ValueDescriptorEvaluator;
import com.braintribe.model.processing.vde.evaluator.api.VdeContext;
import com.braintribe.model.processing.vde.evaluator.api.VdeResult;
import com.braintribe.model.processing.vde.evaluator.api.VdeRuntimeException;
import com.braintribe.model.processing.vde.evaluator.impl.VdeResultImpl;

/**
 * {@link ValueDescriptorEvaluator} for {@link Concatenation}
 * 
 */
public class ConcatenationVde implements ValueDescriptorEvaluator<Concatenation> {

	@Override
	public VdeResult evaluate(VdeContext context, Concatenation valueDescriptor) throws VdeRuntimeException {

		String result = "";
		List<Object> operands = valueDescriptor.getOperands();
		
		if(operands == null){
			throw new VdeRuntimeException("Null operands are not allowed for Cconcatenation");
		}
		
		for (Object rawOperand : operands) {
			Object operand = context.evaluate(rawOperand);
			if (!(operand instanceof String)) {
				throw new VdeRuntimeException("Unable to concatenate operand:" + operand);
			}
			result += (String) operand;
		}
		return new VdeResultImpl(result, false);
	}

}
