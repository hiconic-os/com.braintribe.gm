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
package com.braintribe.model.processing.vde.evaluator.impl.bvd.context;

import java.util.function.Supplier;

import com.braintribe.model.bvd.context.CurrentLocale;
import com.braintribe.model.processing.vde.evaluator.api.ValueDescriptorEvaluator;
import com.braintribe.model.processing.vde.evaluator.api.VdeContext;
import com.braintribe.model.processing.vde.evaluator.api.VdeResult;
import com.braintribe.model.processing.vde.evaluator.api.VdeRuntimeException;
import com.braintribe.model.processing.vde.evaluator.api.aspects.CurrentLocaleAspect;
import com.braintribe.model.processing.vde.evaluator.impl.VdeResultImpl;


/**
 * {@link ValueDescriptorEvaluator} for {@link CurrentLocale}
 * 
 */
public class CurrentLocaleVde implements ValueDescriptorEvaluator<CurrentLocale> {

	@Override
	public VdeResult evaluate(VdeContext context, CurrentLocale valueDescriptor) throws VdeRuntimeException {

		// get the provider associated with the CurrentLocaleAspect
		Supplier<String> provider = context.get(CurrentLocaleAspect.class);
		try {
			return new VdeResultImpl(provider.get(), false);

		} catch (RuntimeException e) {
			throw new VdeRuntimeException("CurrentLocale evaluation failed", e);
		}

	}

}
