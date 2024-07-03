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
package com.braintribe.model.processing.vde.evaluator.api;

/**
 * A wrapper that holds the result from the VDE. It contains the result and a boolean flag to indicate if it is volatile
 * 
 */
public interface VdeResult {

	Object getResult();

	boolean isVolatileValue();

	boolean isNoEvaluationPossible();
	
	String getNoEvaluationReason();
}
