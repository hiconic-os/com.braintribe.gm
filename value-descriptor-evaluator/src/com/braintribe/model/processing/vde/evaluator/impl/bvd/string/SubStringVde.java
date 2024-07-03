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

import com.braintribe.model.bvd.string.SubString;
import com.braintribe.model.processing.vde.evaluator.api.ValueDescriptorEvaluator;
import com.braintribe.model.processing.vde.evaluator.api.VdeContext;
import com.braintribe.model.processing.vde.evaluator.api.VdeResult;
import com.braintribe.model.processing.vde.evaluator.api.VdeRuntimeException;
import com.braintribe.model.processing.vde.evaluator.impl.VdeResultImpl;

/**
 * {@link ValueDescriptorEvaluator} for {@link SubString}
 * 
 */
public class SubStringVde implements ValueDescriptorEvaluator<SubString> {

	@Override
	public VdeResult evaluate(VdeContext context, SubString valueDescriptor) throws VdeRuntimeException {

		Object operand = context.evaluate(valueDescriptor.getOperand());
		Integer startIndex = valueDescriptor.getStartIndex();
		Integer endIndex = valueDescriptor.getEndIndex();
		
		if(startIndex == null){
			throw new VdeRuntimeException("SubString requires a start index");
		}

		if (operand instanceof String) {
			if(endIndex != null){
				return new VdeResultImpl(((String) operand).substring(startIndex, endIndex), false);	
			}
			else{
				return new VdeResultImpl(((String) operand).substring(startIndex), false);
			}
		}
		throw new VdeRuntimeException("SubString is only applicable to String, and not:" + operand);
	}

}
