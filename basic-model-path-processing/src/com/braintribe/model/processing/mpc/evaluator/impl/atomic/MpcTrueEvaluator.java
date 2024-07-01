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
package com.braintribe.model.processing.mpc.evaluator.impl.atomic;

import com.braintribe.logging.Logger;
import com.braintribe.model.generic.path.api.IModelPathElement;
import com.braintribe.model.mpc.atomic.MpcTrue;
import com.braintribe.model.processing.mpc.evaluator.api.MpcEvaluator;
import com.braintribe.model.processing.mpc.evaluator.api.MpcEvaluatorContext;
import com.braintribe.model.processing.mpc.evaluator.api.MpcEvaluatorRuntimeException;
import com.braintribe.model.processing.mpc.evaluator.api.MpcMatch;
import com.braintribe.model.processing.mpc.evaluator.impl.MpcMatchImpl;

/**
 * {@link MpcEvaluator} for {@link MpcTrue}
 * 
 */
public class MpcTrueEvaluator implements MpcEvaluator<MpcTrue> {
	private static Logger logger = Logger.getLogger(MpcTrueEvaluator.class);
	private static boolean debug = logger.isDebugEnabled();
	private static boolean trace = logger.isTraceEnabled();

	@Override
	public MpcMatch matches(MpcEvaluatorContext context, MpcTrue condition, IModelPathElement element) throws MpcEvaluatorRuntimeException {
		if(debug) logger.debug("MpcTrueEvaluator will always return a value, even if null is provided");
		if(trace) logger.debug("None Capture:" + condition.getNoneCapture());
		if (condition.getNoneCapture()) {
			return new MpcMatchImpl(element);
		} else {
			return new MpcMatchImpl(element != null ? element.getPrevious() : null);
		}

	}

	@Override
	public boolean allowsPotentialMatches(MpcTrue condition) {
		return false;
	}

	@Override
	public boolean hasNestedConditions(MpcTrue condition) {
		return false;
	}

}
