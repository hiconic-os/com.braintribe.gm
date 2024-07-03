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
package com.braintribe.model.processing.vde.evaluator.impl.root;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.reflection.EnumType;
import com.braintribe.model.generic.value.EnumReference;
import com.braintribe.model.processing.vde.evaluator.api.ValueDescriptorEvaluator;
import com.braintribe.model.processing.vde.evaluator.api.VdeContext;
import com.braintribe.model.processing.vde.evaluator.api.VdeResult;
import com.braintribe.model.processing.vde.evaluator.api.VdeRuntimeException;
import com.braintribe.model.processing.vde.evaluator.impl.VdeResultImpl;

/**
 * {@link ValueDescriptorEvaluator} for {@link EnumReference}
 * 
 */
public class EnumReferenceVde implements ValueDescriptorEvaluator<EnumReference> {

	@Override
	public VdeResult evaluate(VdeContext context, EnumReference valueDescriptor) throws VdeRuntimeException {
		try {
			EnumType enumType = GMF.getTypeReflection().getType(valueDescriptor.getTypeSignature());
			Enum<?> instance = enumType.getInstance(valueDescriptor.getConstant());
			return new VdeResultImpl(instance, false);
		} catch (Exception e) {
			throw new VdeRuntimeException("Enum Reference evaluation failed for "+ valueDescriptor, e);
		}
	}
}
