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
package com.braintribe.model.processing.vde.evaluator.api;

import com.braintribe.model.generic.value.ValueDescriptor;

/**
 * This is the super type for all value descriptor evaluators
 * 
 * @param <C>
 *            Type of value descriptor
 * 
 */
public interface ValueDescriptorEvaluator<C extends ValueDescriptor> {

	/**
	 * Evaluate the value descriptor based on the context
	 * 
	 * @param context
	 *            Context that the value descriptor is evaluated against
	 * @param valueDescriptor
	 *            The value descriptor that needs evaluation
	 * @return A VdeResult instance that contains the result of the evaluation
	 * @throws VdeRuntimeException
	 */
	VdeResult evaluate(VdeContext context, C valueDescriptor) throws VdeRuntimeException;

}
