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
package com.braintribe.model.processing.mpc.evaluator.impl.structure;

import com.braintribe.logging.Logger;
import com.braintribe.model.generic.path.api.IModelPathElement;
import com.braintribe.model.generic.path.api.IPropertyModelPathElement;
import com.braintribe.model.mpc.structure.MpcPropertyName;
import com.braintribe.model.processing.mpc.evaluator.api.MpcEvaluator;
import com.braintribe.model.processing.mpc.evaluator.api.MpcEvaluatorContext;
import com.braintribe.model.processing.mpc.evaluator.api.MpcEvaluatorRuntimeException;
import com.braintribe.model.processing.mpc.evaluator.api.MpcMatch;
import com.braintribe.model.processing.mpc.evaluator.impl.MpcMatchImpl;

/**
 * {@link MpcEvaluator} for {@link MpcPropertyName}
 *
 */
public class MpcPropertyNameEvaluator implements MpcEvaluator<MpcPropertyName> {

	private static Logger logger = Logger.getLogger(MpcPropertyNameEvaluator.class);
	private static boolean debug = logger.isDebugEnabled();
	
	@Override
	public MpcMatch matches(MpcEvaluatorContext context, MpcPropertyName condition, IModelPathElement element)throws MpcEvaluatorRuntimeException {

		// validate that the property name of the condition is the same as that of the path
		if(debug) logger.debug( "validate that the property name of the condition is the same as that of the path");
		if (element instanceof IPropertyModelPathElement && ((IPropertyModelPathElement) element).getProperty().getName().equals(condition.getPropertyName())) {
			
			MpcMatchImpl result = new MpcMatchImpl(element.getPrevious());
			if(debug) logger.debug( "result" + result);
			return result;
		}
		if(debug) logger.debug( "result" + null);
		return null;
	}

	@Override
	public boolean allowsPotentialMatches(MpcPropertyName condition) {
		return false;
	}

	@Override
	public boolean hasNestedConditions(MpcPropertyName condition) {
		return false;
	}

}
