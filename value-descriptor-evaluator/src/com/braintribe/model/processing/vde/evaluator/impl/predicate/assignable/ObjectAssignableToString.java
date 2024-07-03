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
package com.braintribe.model.processing.vde.evaluator.impl.predicate.assignable;

import com.braintribe.model.bvd.predicate.In;
import com.braintribe.model.processing.vde.evaluator.api.VdeRuntimeException;
import com.braintribe.model.processing.vde.evaluator.api.predicate.PredicateEvalExpert;
import com.braintribe.model.processing.vde.evaluator.impl.predicate.PredicateVdeUtil;

/**
 * Expert for {@link In} that operates on left hand side operand of type
 * Object and right hand side operand of type String
 * 
 */
public class ObjectAssignableToString implements PredicateEvalExpert<Object, String> {

	private static ObjectAssignableToString instance = null;

	protected ObjectAssignableToString() {
		// empty
	}

	public static ObjectAssignableToString getInstance() {
		if (instance == null) {
			instance = new ObjectAssignableToString();
		}
		return instance;
	}
	
	@Override
	public Object evaluate(Object leftOperand, String rightOperand) throws VdeRuntimeException {
		return PredicateVdeUtil.instanceOf(leftOperand, rightOperand, true);
	}

}
