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
package com.braintribe.model.processing.vde.evaluator.impl.bvd.collection;

import java.util.Collection;

import com.braintribe.model.bvd.convert.collection.RemoveNulls;
import com.braintribe.model.processing.vde.evaluator.api.ValueDescriptorEvaluator;
import com.braintribe.model.processing.vde.evaluator.api.VdeContext;
import com.braintribe.model.processing.vde.evaluator.api.VdeResult;
import com.braintribe.model.processing.vde.evaluator.api.VdeRuntimeException;
import com.braintribe.model.processing.vde.evaluator.impl.VdeResultImpl;
import com.braintribe.utils.lcd.CollectionTools;

/**
 * {@link ValueDescriptorEvaluator} for {@link RemoveNulls}
 * 
 */
public class RemoveNullsVde implements ValueDescriptorEvaluator<RemoveNulls> {

	@Override
	public VdeResult evaluate(VdeContext context, RemoveNulls valueDescriptor) throws VdeRuntimeException {
		Object operand = context.evaluate(valueDescriptor.getCollection());
		
		if (!(operand instanceof Collection<?>)) {
			throw new VdeRuntimeException("RemoveNull is not applicable to:" + operand);
		}

		Collection<?> collection = (Collection<?>) operand;
		CollectionTools.removeNulls(collection);
		return new VdeResultImpl(collection, false);
	}

}
