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
package com.braintribe.model.processing.vde.evaluator.impl.bvd.convert;

import com.braintribe.model.bvd.convert.ToBoolean;
import com.braintribe.model.processing.vde.evaluator.api.ValueDescriptorEvaluator;
import com.braintribe.model.processing.vde.evaluator.api.VdeContext;
import com.braintribe.model.processing.vde.evaluator.api.VdeResult;
import com.braintribe.model.processing.vde.evaluator.api.VdeRuntimeException;
import com.braintribe.model.processing.vde.evaluator.impl.VdeResultImpl;

/**
 * {@link ValueDescriptorEvaluator} for {@link ToBoolean}
 * 
 */
public class ToBooleanVde implements ValueDescriptorEvaluator<ToBoolean> {

	@Override
	public VdeResult evaluate(VdeContext context, ToBoolean valueDescriptor) throws VdeRuntimeException {
		
		Object operand = context.evaluate(valueDescriptor.getOperand());
		
		if (!(operand instanceof String)) {

			throw new VdeRuntimeException("Convert to Boolean is not applicable to:" + operand);
		}

		Object format = valueDescriptor.getFormat();
		
		if(format != null){
			throw new VdeRuntimeException("Format is not supported for ToBoolean yet, format:" + format);
		}

		Boolean result = Boolean.parseBoolean((String) operand);
		
		return new VdeResultImpl(result, false);

	}

}
