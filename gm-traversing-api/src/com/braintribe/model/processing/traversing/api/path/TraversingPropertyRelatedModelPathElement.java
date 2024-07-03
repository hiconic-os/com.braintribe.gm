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
package com.braintribe.model.processing.traversing.api.path;

import com.braintribe.model.generic.path.api.IPropertyRelatedModelPathElement;
import com.braintribe.model.generic.reflection.GenericModelType;

/**
 * This {@link TraversingModelPathElement} collects common properties of all property based elements such as:
 * 
 * <ul>
 * <li>{@link TraversingPropertyModelPathElement}</li>
 * <li>{@link TraversingListItemModelPathElement}</li>
 * <li>{@link TraversingSetItemModelPathElement}</li>
 * <li>{@link TraversingMapKeyModelPathElement}</li>
 * <li>{@link TraversingMapValueModelPathElement}</li>
 * <li></li>
 * </ul>
 * 
 * @author dirk.scheffler
 * @author pit.steinlin
 * @author peter.gazdik
 */
public abstract class TraversingPropertyRelatedModelPathElement extends TraversingModelPathElement implements IPropertyRelatedModelPathElement {

	public TraversingPropertyRelatedModelPathElement(TraversingModelPathElement previous, Object value, GenericModelType type) {
		super(previous, value, type);
	}

	protected TraversingPropertyRelatedModelPathElement(TraversingModelPathElement previous) {
		super(previous);
	}

}
