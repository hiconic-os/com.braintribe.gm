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
package com.braintribe.model.processing.vde.evaluator.impl.predicate.greater;

import com.braintribe.model.bvd.predicate.Greater;
import com.braintribe.model.processing.vde.evaluator.api.VdeRuntimeException;
import com.braintribe.model.processing.vde.evaluator.api.predicate.PredicateEvalExpert;

/**
 * Expert for {@link Greater} that operates on left hand side operand of type
 * Enum and right hand side operand of type Enum
 * 
 */
public class EnumGreater implements PredicateEvalExpert<Enum<?>, Enum<?>> {

	private static EnumGreater instance = null;

	protected EnumGreater() {
		// empty
	}

	public static EnumGreater getInstance() {
		if (instance == null) {
			instance = new EnumGreater();
		}
		return instance;
	}

	@Override
	public Object evaluate(Enum<?> leftOperand, Enum<?> rightOperand) throws VdeRuntimeException {

		@SuppressWarnings("rawtypes")
		Class<? extends Enum> class1 = leftOperand.getClass();
		@SuppressWarnings("rawtypes")
		Class<? extends Enum> class2 = rightOperand.getClass();

		if (class1.equals(class2)) {
			return leftOperand.ordinal() > rightOperand.ordinal();

		} else {
			throw new VdeRuntimeException("Left and right Enum must be of same type for Enum Comparison");
		}
	}

}
