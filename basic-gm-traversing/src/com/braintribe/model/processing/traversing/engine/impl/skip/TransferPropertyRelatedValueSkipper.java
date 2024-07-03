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
package com.braintribe.model.processing.traversing.engine.impl.skip;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.path.api.ModelPathElementType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.processing.traversing.api.GmTraversingContext;
import com.braintribe.model.processing.traversing.api.GmTraversingException;
import com.braintribe.model.processing.traversing.api.SkipUseCase;
import com.braintribe.model.processing.traversing.api.path.TraversingModelPathElement;
import com.braintribe.model.processing.traversing.api.path.TraversingPropertyModelPathElement;
import com.braintribe.model.processing.traversing.engine.api.skip.Skipper;
import com.braintribe.model.processing.traversing.engine.api.usecase.DefaultSkipUseCase;
import com.braintribe.model.processing.traversing.impl.visitors.GmTraversingVisitorAdapter;

/**
 * A {@link Skipper} that is triggered if the path element is of type {@link TraversingPropertyModelPathElement}
 */
public abstract class TransferPropertyRelatedValueSkipper extends GmTraversingVisitorAdapter implements Skipper {

	private SkipUseCase skipUseCase = DefaultSkipUseCase.INSTANCE;

	private final PropertySkippingContextImpl propertySkippingContext = new PropertySkippingContextImpl();
	
	@Override
	public SkipUseCase getSkipUseCase() {
		return skipUseCase;
	}

	@Override
	public void setSkipUseCase(SkipUseCase skipUseCase) {
		this.skipUseCase = skipUseCase;
	}

	@Override
	public void onElementEnter(GmTraversingContext context, TraversingModelPathElement pathElement) throws GmTraversingException {
		if (pathElement.getElementType() == ModelPathElementType.Property) {

			propertySkippingContext.context = context;
			propertySkippingContext.propertyPathElement = (TraversingPropertyModelPathElement) pathElement;
			
			if (shouldSkipProperty(propertySkippingContext)) {
				// update a skipUseCase in context
				context.skipDescendants(getSkipUseCase());
			}
		}
	}

	protected abstract boolean shouldSkipProperty(PropertySkippingContext context);

	static class PropertySkippingContextImpl implements PropertySkippingContext {

		public GmTraversingContext context;
		public TraversingPropertyModelPathElement propertyPathElement;

		
		@Override
		public GenericEntity getInstanceToBeCloned() {
			return (GenericEntity) propertyPathElement.getPrevious().getValue();
		}

		@Override
		public GenericEntity getClonedInstance() {
			return (GenericEntity) context.getSharedCustomValue(propertyPathElement.getPrevious());
		}

		@Override
		public Property getProperty() {
			return propertyPathElement.getProperty();
		}

		@Override
		public GmTraversingContext getTraversingContext() {
			return context;
		}

		@Override
		public TraversingPropertyModelPathElement getPropertyPathElement() {
			return propertyPathElement;
		}
		
		
	}
	
}
