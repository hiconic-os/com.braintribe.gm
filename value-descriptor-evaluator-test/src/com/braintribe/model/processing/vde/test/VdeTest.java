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
package com.braintribe.model.processing.vde.test;

import com.braintribe.model.processing.vde.evaluator.VDE;
import com.braintribe.model.processing.vde.evaluator.api.VdeContextAspect;
import com.braintribe.model.processing.vde.evaluator.api.VdeEvaluationMode;
import com.braintribe.model.processing.vde.evaluator.api.VdeRuntimeException;

/**
 * @author peter.gazdik
 */
public abstract class VdeTest {

	protected Object evaluate(Object vdeOrValue) {
		return VDE.evaluate(vdeOrValue);
	}

	protected <T, A extends VdeContextAspect<? super T>> Object evaluateWith(Class<A> aspect, T value, Object object) throws VdeRuntimeException {
		return VDE.evaluateWith(aspect, value, object);
	}

	protected Object evaluateWithEvaluationMode(Object value, VdeEvaluationMode evalMode) {
		return VDE.evaluate().withEvaluationMode(evalMode).forValue(value);
	}

}
