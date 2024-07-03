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
package com.braintribe.model.processing.traversing.engine.impl.clone;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.processing.traversing.api.GmTraversingContext;
import com.braintribe.model.processing.traversing.api.path.TraversingPropertyModelPathElement;
import com.braintribe.model.processing.traversing.engine.api.customize.PropertyTransferContext;

/**
 * @author peter.gazdik
 */
class PropertyTransferContextImpl implements PropertyTransferContext {

	public GmTraversingContext traversingContext; 
	public TraversingPropertyModelPathElement propertyPathElement;
	public GenericEntity clonedEntity;

	@Override
	public GmTraversingContext getTraversingContext() {
		return traversingContext;
	}

	@Override
	public TraversingPropertyModelPathElement getPropertyPathElement() {
		return propertyPathElement;
	}

	@Override
	public Property getProperty() {
		return propertyPathElement.getProperty();
	}

	@Override
	public GenericEntity getInstanceToBeCloned() {
		return (GenericEntity) propertyPathElement.getPrevious().getValue();
	}

	@Override
	public GenericEntity getClonedInstance() {
		return clonedEntity;
//		return traversingContext.getSharedCustomValue(propertyPathElement.getPrevious());
	}

	@Override
	public Object getValueToBeCloned() {
		return getProperty().get(getInstanceToBeCloned());
	}


}
