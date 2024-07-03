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
package com.braintribe.model.processing.mpc.evaluator.impl.value.vde;

import com.braintribe.logging.Logger;
import com.braintribe.model.generic.path.api.IModelPathElement;
import com.braintribe.model.mpc.value.MpcElementValue;
import com.braintribe.model.processing.mpc.evaluator.api.MpcEvaluatorRuntimeException;
import com.braintribe.model.processing.mpc.evaluator.impl.value.vde.aspect.MpcElementValueAspect;
import com.braintribe.model.processing.mpc.evaluator.utils.MpcEvaluationTools;
import com.braintribe.model.processing.vde.evaluator.api.ValueDescriptorEvaluator;
import com.braintribe.model.processing.vde.evaluator.api.VdeContext;
import com.braintribe.model.processing.vde.evaluator.api.VdeResult;
import com.braintribe.model.processing.vde.evaluator.api.VdeRuntimeException;
import com.braintribe.model.processing.vde.evaluator.impl.VdeResultImpl;

/**
 * {@link ValueDescriptorEvaluator} for {@link MpcElementValue}
 * 
 */
public class MpcElementValueVde implements ValueDescriptorEvaluator<MpcElementValue> {

	private static Logger logger = Logger.getLogger(MpcElementValueVde.class);
	private static boolean debug = logger.isDebugEnabled();
	
	public VdeResult evaluate(VdeContext context, MpcElementValue valueDescriptor) throws VdeRuntimeException {
		
		if (debug) logger.debug("get the value for the aspect of MpcElementValueAspect");
		IModelPathElement element = context.get(MpcElementValueAspect.class);
		Object evaluationResult = null;
		
		try {
			evaluationResult = MpcEvaluationTools.resolve(valueDescriptor.getElementAxis(), element);
			if (debug) logger.debug("evaluationResult " + evaluationResult);
		} catch (MpcEvaluatorRuntimeException e) {
			logger.error("MpcElementValueVde failed to resolve the MpcElementValue",e);
			throw new VdeRuntimeException("MpcElementValueVde failed to resolve the MpcElementValue", e);
		}
		
		// TODO validate that the result will not be volatile
		return new VdeResultImpl(evaluationResult, false);
	}

}
