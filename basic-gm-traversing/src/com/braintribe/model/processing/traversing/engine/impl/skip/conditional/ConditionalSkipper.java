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
package com.braintribe.model.processing.traversing.engine.impl.skip.conditional;

import com.braintribe.model.processing.traversing.api.GmTraversingSkippingCriteria;
import com.braintribe.model.processing.traversing.engine.api.skip.Skipper;

/**
 * Represents the parent of any {@link Skipper} that a requires a condition to
 * evaluate.
 *
 * If a skip occurs, the extent of it is defined by
 * {@link GmTraversingSkippingCriteria}
 */
public interface ConditionalSkipper extends Skipper {

	GmTraversingSkippingCriteria getSkippingCriteria();
	void setSkippingCriteria(GmTraversingSkippingCriteria skippingCriteria);

}
