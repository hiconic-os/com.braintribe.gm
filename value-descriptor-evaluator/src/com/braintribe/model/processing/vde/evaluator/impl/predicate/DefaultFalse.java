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
package com.braintribe.model.processing.vde.evaluator.impl.predicate;

import com.braintribe.model.processing.vde.evaluator.api.VdeRuntimeException;
import com.braintribe.model.processing.vde.evaluator.api.predicate.PredicateEvalExpert;

/**
 * A {@link PredicateEvalExpert} that returns false always
 * 
 */
public class DefaultFalse implements PredicateEvalExpert<Object, Object> {

	private static DefaultFalse instance = null;

	protected DefaultFalse() {
		// empty
	}

	public static DefaultFalse getInstance() {
		if (instance == null) {
			instance = new DefaultFalse();
		}
		return instance;
	}

	@Override
	public Object evaluate(Object leftOperand, Object rightOperand) throws VdeRuntimeException {
		return Boolean.FALSE;
	}

}
