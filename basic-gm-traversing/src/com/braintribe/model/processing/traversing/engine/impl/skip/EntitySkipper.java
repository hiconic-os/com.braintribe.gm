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
import com.braintribe.model.generic.reflection.TypeCode;
import com.braintribe.model.processing.traversing.api.GmTraversingContext;
import com.braintribe.model.processing.traversing.api.GmTraversingException;
import com.braintribe.model.processing.traversing.api.SkipUseCase;
import com.braintribe.model.processing.traversing.api.path.TraversingModelPathElement;
import com.braintribe.model.processing.traversing.engine.api.skip.Skipper;
import com.braintribe.model.processing.traversing.engine.api.usecase.DefaultSkipUseCase;
import com.braintribe.model.processing.traversing.impl.visitors.GmTraversingVisitorAdapter;

public abstract class EntitySkipper extends GmTraversingVisitorAdapter implements Skipper {

	private SkipUseCase skipUseCase = DefaultSkipUseCase.INSTANCE;

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
		Object value = pathElement.getValue();
		if (value == null)
			return;

		if (pathElement.getType().getTypeCode() != TypeCode.entityType) {
			return;
		}

		if (shouldSkipEntity(context, (GenericEntity) value, pathElement)) {
			// update a skipUseCase in context
			context.skipDescendants(getSkipUseCase());
		}
	}

	protected abstract boolean shouldSkipEntity(GmTraversingContext context, GenericEntity entity, TraversingModelPathElement pathElement);

}
