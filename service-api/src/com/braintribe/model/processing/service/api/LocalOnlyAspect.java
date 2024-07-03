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
package com.braintribe.model.processing.service.api;

import com.braintribe.model.generic.eval.EvalContextAspect;
import com.braintribe.model.generic.pr.AbsenceInformation;

/**
 * <p>
 * A {@link ServiceRequestContextAspect} and {@link EvalContextAspect} for propagating whether only local targets are
 * to be taken into account.
 * 
 * <p>
 * This is particularly useful to avoid processing requests through redirecting components (like proxies) when the
 * actual processors are also requested to process such request (e.g.: when the request is fanned out or multicasted).
 * 
 */
public interface LocalOnlyAspect extends ServiceRequestContextAspect<Boolean>, EvalContextAspect<Boolean> {

	Object absentResult = AbsenceInformation.T.create();

}
