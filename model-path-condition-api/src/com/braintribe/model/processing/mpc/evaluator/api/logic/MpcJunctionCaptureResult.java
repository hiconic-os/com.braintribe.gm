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
package com.braintribe.model.processing.mpc.evaluator.api.logic;

import com.braintribe.model.generic.path.api.IModelPathElement;
import com.braintribe.model.mpc.logic.MpcJunctionCapture;
import com.braintribe.model.processing.mpc.evaluator.api.MpcMatch;

/**
 * A helper class used by {@link MpcConjunctionEvaluator} and
 * {@link MpcDisjunctionEvaluator} to assist with evaluation of
 * {@link MpcJunctionCapture} condition
 * 
 */
public interface MpcJunctionCaptureResult {

	void setPathLength(int length);

	int getPathLength();

	void setReturnPath(IModelPathElement path);

	void setReturnPath(MpcMatch match);

	IModelPathElement getReturnPath();

	MpcMatch getMatchResult();
}
