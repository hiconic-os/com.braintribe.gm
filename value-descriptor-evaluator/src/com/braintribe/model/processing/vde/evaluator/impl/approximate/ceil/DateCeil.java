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
package com.braintribe.model.processing.vde.evaluator.impl.approximate.ceil;

import java.util.Date;

import com.braintribe.model.bvd.math.Ceil;
import com.braintribe.model.processing.vde.evaluator.api.VdeRuntimeException;
import com.braintribe.model.processing.vde.evaluator.api.approximate.ApproximateEvalExpert;
import com.braintribe.model.processing.vde.evaluator.impl.approximate.ApproximateVdeUtil;
import com.braintribe.model.time.DateOffset;
import com.braintribe.model.time.DateOffsetUnit;

/**
 * Expert for {@link Ceil} that operates on value of type Date and precision of type DateOffset 
 *
 */
public class DateCeil implements ApproximateEvalExpert<Date, DateOffset> {

	private static DateCeil instance = null;

	protected DateCeil() {
		// empty
	}

	public static DateCeil getInstance() {
		if (instance == null) {
			instance = new DateCeil();
		}
		return instance;
	}

	@Override
	public Object evaluate(Date date, DateOffset offset) throws VdeRuntimeException {

		boolean offsetValidation = ApproximateVdeUtil.validateDateOffset(offset);
		
		if(!offsetValidation){
			throw new VdeRuntimeException("Date offset " + offset + " is not valid for an approximation");
		}
		DateOffsetUnit offsetUnit = offset.getOffset();

		// get original value
		int origValue = ApproximateVdeUtil.getOriginalDateComponentValue(date, offsetUnit);
		double fractionOfRealUnit = ApproximateVdeUtil.getSmallerUnitValue(date, offsetUnit);
		// reset everything lower than current threshold
		Date freshDate = ApproximateVdeUtil.resetBelowThreshold(date, offsetUnit);

		int offsetValue = offset.getValue();
		Number ceil = ApproximateVdeUtil.getCeil(Double.valueOf(origValue + fractionOfRealUnit), Integer.valueOf(offsetValue));

		// set updated value;
		Date evaluatedDate = ApproximateVdeUtil.setOriginalDateComponentValue(freshDate, offsetUnit, ceil.intValue());

		return evaluatedDate;
	}

}
