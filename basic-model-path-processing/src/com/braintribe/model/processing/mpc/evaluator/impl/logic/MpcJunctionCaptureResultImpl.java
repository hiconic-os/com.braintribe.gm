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
package com.braintribe.model.processing.mpc.evaluator.impl.logic;

import com.braintribe.logging.Logger;
import com.braintribe.model.generic.path.api.IModelPathElement;
import com.braintribe.model.mpc.logic.MpcJunctionCapture;
import com.braintribe.model.processing.mpc.evaluator.api.MpcMatch;
import com.braintribe.model.processing.mpc.evaluator.api.logic.MpcJunctionCaptureResult;
import com.braintribe.model.processing.mpc.evaluator.impl.MpcMatchImpl;

/**
 * A helper class used by {@link MpcConjunctionEvaluator} and
 * {@link MpcDisjunctionEvaluator} to assist with evaluation of
 * {@link MpcJunctionCapture} condition
 * 
 */
public class MpcJunctionCaptureResultImpl implements MpcJunctionCaptureResult {

	private static Logger logger = Logger.getLogger(MpcJunctionCaptureResultImpl.class);
	private static boolean trace = logger.isTraceEnabled();

	private MpcMatchImpl matchResult;
	private int length;

	public MpcJunctionCaptureResultImpl() {
		matchResult = new MpcMatchImpl(null);
	}

	@Override
	public void setPathLength(int length) {
		this.length = length;
	}

	@Override
	public int getPathLength() {
		return length;
	}

	@Override
	public void setReturnPath(IModelPathElement path) {
		this.matchResult.setPath(path);
		this.length = path.getDepth();
	}

	@Override
	public void setReturnPath(MpcMatch match) {
		if (trace)
			logger.trace("set return path with " + match);
		if (match == null) {
			matchResult = null;
		} else {
			this.matchResult.setPath(match.getPath());
			if (trace)
				logger.trace("set length where path " + match.getPath());
			this.length = match.getPath() == null ? -1 : match.getPath().getDepth();
			if (trace)
				logger.trace("length" + this.length);
		}
	}

	@Override
	public IModelPathElement getReturnPath() {
		return (getMatchResult() != null) ? getMatchResult().getPath() : null;
	}

	@Override
	public MpcMatch getMatchResult() {
		return matchResult;
	}
}
