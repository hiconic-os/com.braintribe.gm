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
package com.braintribe.model.processing.vde.evaluator.impl.bvd.logic;

import java.util.List;

import com.braintribe.model.bvd.logic.Disjunction;
import com.braintribe.model.processing.vde.evaluator.api.ValueDescriptorEvaluator;
import com.braintribe.model.processing.vde.evaluator.api.VdeContext;
import com.braintribe.model.processing.vde.evaluator.api.VdeResult;
import com.braintribe.model.processing.vde.evaluator.api.VdeRuntimeException;
import com.braintribe.model.processing.vde.evaluator.impl.VdeResultImpl;

/**
 * {@link ValueDescriptorEvaluator} for {@link Disjunction}
 * 
 */
public class DisjunctionVde implements ValueDescriptorEvaluator<Disjunction> {

	@Override
	public VdeResult evaluate(VdeContext context, Disjunction valueDescriptor) throws VdeRuntimeException {

		Boolean result = false;
		List<Object> operands = valueDescriptor.getOperands();
		
		if(operands == null){
			throw new VdeRuntimeException("Disjunction operands can not be null");
		}
		
		for (Object operand: valueDescriptor.getOperands()){
			
			Object evaluatedOperand = context.evaluate(operand);
			if(!(evaluatedOperand instanceof Boolean)){
				throw new VdeRuntimeException("Disjunction operates on BooleanDescriptor Values only. This is not valid:" + evaluatedOperand);
			}
			result = result || (Boolean)evaluatedOperand;
		}
		
		return new VdeResultImpl(result, false);
	}

}
