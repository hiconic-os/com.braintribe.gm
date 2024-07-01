// ============================================================================
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
// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
// ============================================================================
package com.braintribe.model.processing.mpc.evaluator.impl.generic;

import com.braintribe.logging.Logger;
import com.braintribe.model.generic.path.api.IModelPathElement;
import com.braintribe.model.mpc.ModelPathCondition;
import com.braintribe.model.mpc.value.MpcElementValue;
import com.braintribe.model.processing.mpc.evaluator.api.MpcEvaluatorContext;
import com.braintribe.model.processing.mpc.evaluator.api.MpcEvaluatorRuntimeException;
import com.braintribe.model.processing.mpc.evaluator.impl.value.vde.aspect.MpcElementValueAspect;
import com.braintribe.model.processing.vde.evaluator.api.ValueDescriptorEvaluator;
import com.braintribe.model.processing.vde.evaluator.api.VdeContext;
import com.braintribe.model.processing.vde.evaluator.api.VdeResult;
import com.braintribe.model.processing.vde.evaluator.api.VdeRuntimeException;
import com.braintribe.model.processing.vde.evaluator.impl.VdeResultImpl;

/**
 * {@link ValueDescriptorEvaluator} for {@link MpcElementValue}
 * 
 */
public class MpcGenericVde implements ValueDescriptorEvaluator<ModelPathCondition> {

	private static Logger logger = Logger.getLogger(MpcGenericVde.class);
	private static boolean debug = logger.isDebugEnabled();
	
	
	private MpcEvaluatorContext mpcContext;
	
	public MpcGenericVde(MpcEvaluatorContext mpcContext){
		this.mpcContext = mpcContext; 
	}
	
	public VdeResult evaluate(VdeContext context, ModelPathCondition valueDescriptor) throws VdeRuntimeException {
		
		Object evaluationResult = null;
		if (debug) logger.debug("get the value for the aspect of MpcElementValueAspect");
		IModelPathElement element = context.get(MpcElementValueAspect.class);
		
		try {
			
			if (debug) logger.debug("get the value for the aspect of MpcElementValueAspect");
			evaluationResult = mpcContext.matches(valueDescriptor, element);
			if (debug) logger.debug("evaluationResult " + evaluationResult);
			
		} catch (MpcEvaluatorRuntimeException e) {
			logger.error("MpcGenericVde failed to resolve the ModelPathCondition" + valueDescriptor ,e);
			throw new VdeRuntimeException("MpcGenericVde failed to resolve the ModelPathCondition" + valueDescriptor ,e);
		}
		
		return new VdeResultImpl(evaluationResult, false);
	}

}
