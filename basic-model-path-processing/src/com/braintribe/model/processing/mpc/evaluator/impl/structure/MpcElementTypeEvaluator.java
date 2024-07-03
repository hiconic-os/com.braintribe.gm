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
import com.braintribe.model.generic.path.api.ModelPathElementType;
import com.braintribe.model.mpc.structure.MpcElementType;
import com.braintribe.model.processing.mpc.evaluator.api.MpcEvaluator;
import com.braintribe.model.processing.mpc.evaluator.api.MpcEvaluatorContext;
import com.braintribe.model.processing.mpc.evaluator.api.MpcEvaluatorRuntimeException;
import com.braintribe.model.processing.mpc.evaluator.api.MpcMatch;
import com.braintribe.model.processing.mpc.evaluator.impl.MpcMatchImpl;

/**
 * {@link MpcEvaluator} for {@link MpcElementType}
 *
 */
public class MpcElementTypeEvaluator implements MpcEvaluator<MpcElementType> {

	private static Logger logger = Logger.getLogger(MpcElementTypeEvaluator.class);
	private static boolean debug = logger.isDebugEnabled();
	
	@Override
	public MpcMatch matches(MpcEvaluatorContext context, MpcElementType condition, IModelPathElement element) throws MpcEvaluatorRuntimeException {

		MpcMatchImpl result = new MpcMatchImpl(element.getPrevious());

		if(debug) logger.debug(" compare the Element type of the condition (which is GmModelPathElementType)"+ condition.getElementType() +" against the element type of the path (which is ModelPathElementType)" + element.getElementType() );
		switch (condition.getElementType()) {

			case ListItem:

				return (element.getElementType() == ModelPathElementType.ListItem) ? result : null;

			case MapKey:

				return (element.getElementType() == ModelPathElementType.MapKey) ? result : null;

			case MapValue:

				return (element.getElementType() == ModelPathElementType.MapValue) ? result : null;

			case Property:

				return (element.getElementType() == ModelPathElementType.Property) ? result : null;

			case EntryPoint: //As per call with Dirk on 16.01.2015, treat EntryPoint as Root. 
			case Root:

				return (element.getElementType() == ModelPathElementType.Root) ? result : null;

			case SetItem:

				return (element.getElementType() == ModelPathElementType.SetItem) ? result : null;

			default:

				throw new MpcEvaluatorRuntimeException("Unsupported MpcElementType type :" + condition.getElementType());
		}

	}

	@Override
	public boolean allowsPotentialMatches(MpcElementType condition) {
		return false;
	}

	@Override
	public boolean hasNestedConditions(MpcElementType condition) {
		return false;
	}

}
