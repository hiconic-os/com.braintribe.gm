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
package com.braintribe.model.processing.traversing.engine.impl.clone.legacy;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.CloningContext;
import com.braintribe.model.processing.traversing.api.GmTraversingContext;
import com.braintribe.model.processing.traversing.api.path.TraversingModelPathElement;
import com.braintribe.model.processing.traversing.engine.impl.walk.BasicModelWalkerCustomization;

/**
 * @author peter.gazdik
 */
public class CloningContextBasedBasicModelWalkerCustomization extends BasicModelWalkerCustomization {

	private final CloningContext cc;

	/**
	 * @param cc
	 *            a CloningContext which is compatible with GMT, most likely something that is explicitly marked as
	 *            {@link GmtCompatibleCloningContext}, or at least follows the same limitations.
	 */
	public CloningContextBasedBasicModelWalkerCustomization(CloningContext cc) {
		this.cc = cc;
	}

	@Override
	public TraversingModelPathElement substitute(GmTraversingContext context, TraversingModelPathElement pathElement) {
		Object value = pathElement.getValue();
		if (!(value instanceof GenericEntity))
			return pathElement;

		GenericEntity substitute = cc.preProcessInstanceToBeCloned((GenericEntity) value);
		if (value != substitute)
			pathElement.substituteValue(substitute);
			
		return pathElement;
	}
}
