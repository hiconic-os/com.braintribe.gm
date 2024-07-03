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

import java.util.Date;

import com.braintribe.model.bvd.convert.ToString;
import com.braintribe.model.processing.vde.evaluator.api.ValueDescriptorEvaluator;
import com.braintribe.model.processing.vde.evaluator.api.VdeContext;
import com.braintribe.model.processing.vde.evaluator.api.VdeResult;
import com.braintribe.model.processing.vde.evaluator.api.VdeRuntimeException;
import com.braintribe.model.processing.vde.evaluator.impl.VdeResultImpl;
import com.braintribe.utils.format.lcd.FormatTool;

/**
 * {@link ValueDescriptorEvaluator} for {@link ToString}
 * 
 */
public class ToStringVde implements ValueDescriptorEvaluator<ToString> {

	@Override
	public VdeResult evaluate(VdeContext context, ToString valueDescriptor) throws VdeRuntimeException {

		Object result = null;
		Object operand = context.evaluate(valueDescriptor.getOperand());

		Object format = valueDescriptor.getFormat();

		if (operand instanceof Date) {

			Date date = (Date) operand;
			if (format != null) {
				if (format instanceof String) {
					try {
						result =  FormatTool.getExpert().getDateFormat().formatDate(date, (String) format);
					} catch (Exception e) {
						throw new VdeRuntimeException("Format for ToString with Date operand should follow SimpleDateFormat format", e);
					}
				} else {
					throw new VdeRuntimeException("Convert ToString with Date operand is not applicable for format:" + format);
				}

			} else {
				result = date.toString();
			}

		} else if (operand instanceof Number) {
			if(format != null){
				if(format instanceof String){
					result = FormatTool.getExpert().getNumberFormat().format((Number)operand,(String) format);					
				}
				else{
					throw new VdeRuntimeException("Conver ToString with from any number only accepts Format of applicable with DecimalFormat and not:" + format);
				}
			}
			else{
				result = operand.toString();
			}

		} else {
			result = operand.toString();
		}

		return new VdeResultImpl(result, false);
	}
}
